# Deploying the Spring Boot Backend with Docker, Docker Compose, GitHub Actions, and RDS

This document covers deploying just the backend API as a Docker container on a single EC2 host, using Docker Compose to run it, RDS as the managed PostgreSQL database, Nginx as a reverse proxy and TLS terminator in front of it, and GitHub Actions to build, push, and deploy on every push to main.

The flow is that a push to `main` triggers a GitHub Actions workflow, which builds the backend image and pushes it to GitHub Container Registry (GHCR). A second job then SSHes into the EC2 instance, pulls the new image, and runs `docker compose up -d`, which recreates the backend container. Nginx sits in front of it, terminating TLS on 443 and reverse-proxying requests to the backend container, which is never exposed directly to the internet, only reachable from Nginx over the Docker network or localhost.

## 1. Provisioning the EC2 Instance and Installing Docker

Launch an Ubuntu 24.04 EC2 instance. A `t3.small` is a reasonable starting size for a Spring Boot API under moderate load. In the security group, open port 22 to your IP only, and open ports 80 and 443 to the world. Port 8080, where the backend listens, does not need to be opened at all, since only Nginx on the same host will ever talk to it.

Connect over SSH and install Docker using the official convenience script:

```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

Log out and back in (or run `newgrp docker`) so the group change takes effect, then confirm both Docker and the Compose plugin are available:

```bash
docker --version
docker compose version
```

Also install Nginx directly on the host rather than as a container, since with no frontend to serve, Nginx's only job here is TLS termination and reverse proxying, which is simpler to manage with the standard package and Certbot's native Nginx plugin than by containerizing it:

```bash
sudo apt update
sudo apt install -y nginx
```

## 2. Setting Up RDS (PostgreSQL)

Create the RDS PostgreSQL instance in the same VPC as the EC2 instance. Set "Public access" to No, since the database should only ever be reached from inside AWS. For the security group attached to RDS, add an inbound rule allowing port 5432 with the source set to the EC2 instance's security group, not an IP range. This means the database is reachable only from traffic originating on that specific EC2 host, regardless of whether the request comes from a container or a process running directly on the host.

Note the RDS endpoint once it's provisioned, something like `tenanttable-db.abc123xyz.us-east-1.rds.amazonaws.com`; this becomes the `DB_HOST` value passed into the container as an environment variable. You can sanity-check connectivity from the EC2 host before wiring it into the app:

```bash
sudo apt install -y postgresql-client
psql -h your-rds-endpoint.rds.amazonaws.com -U your_master_username -d postgres
```

## 3. Dockerizing the Backend

Add a `Dockerfile` at the root of the backend project, using a multi-stage build so the shipped image contains only a JRE and the built jar, not the full JDK and Maven toolchain:

```dockerfile
# Stage 1: build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Copying `pom.xml` and running `dependency:go-offline` before copying the rest of the source lets Docker cache the dependency-download layer across builds, so a change to application code alone doesn't force re-downloading the whole dependency tree on every CI run.

Production configuration stays profile-based, with values coming from environment variables rather than being hardcoded:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
  profiles:
    active: prod
```

`ddl-auto: validate` rather than `update` is deliberate in production: schema changes should go through a migration tool like Flyway or Liquibase, checked into version control, rather than having Hibernate alter tables automatically on startup.

## 4. docker-compose.yml

Place a `docker-compose.yml` on the server, for example at `/home/ubuntu/tenanttable/docker-compose.yml`:

```yaml
services:
  backend:
    image: ghcr.io/your-username/tenanttable-api:latest
    restart: always
    env_file:
      - .env
    ports:
      - "127.0.0.1:8080:8080"
```

The port mapping binds only to `127.0.0.1` on the host rather than `0.0.0.0`, which means the container is reachable from Nginx running on the same machine, but not from the public internet directly, even though port 8080 isn't listed in the security group at all — this is a second, redundant layer of protection on top of the security group, and it's worth having both rather than relying on the security group alone.

`env_file` points at a `.env` file next to the compose file, holding database credentials and other secrets. This file should never be committed to git, and its permissions should be restricted:

```bash
chmod 600 .env
```

A typical `.env`:

```
DB_HOST=tenanttable-db.abc123xyz.us-east-1.rds.amazonaws.com
DB_NAME=tenanttable
DB_USERNAME=your_master_username
DB_PASSWORD=your_actual_password
JWT_SECRET=your_jwt_secret
```

Bring the container up:

```bash
cd /home/ubuntu/tenanttable
docker compose up -d
docker compose logs -f backend
```

## 5. Nginx Reverse Proxy

Create `/etc/nginx/sites-available/tenanttable-api`:

```nginx
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Using a dedicated subdomain like `api.yourdomain.com` rather than a path prefix like `/api` on a shared domain is simpler here since there's no frontend on the same host competing for the root path; it also keeps the Nginx config trivial, a single `location /` block forwarding everything straight through.

Enable the site and reload Nginx:

```bash
sudo ln -s /etc/nginx/sites-available/tenanttable-api /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx
```

## 6. DNS and HTTPS

Point an A record for `api.yourdomain.com` at the EC2 instance's Elastic IP (allocate one if you haven't, so the address survives a stop/start cycle). Once DNS has propagated, request a certificate with Certbot's Nginx plugin, which will edit the site config directly to add the `listen 443 ssl` block and the certificate paths:

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d api.yourdomain.com
```

Certbot also installs a systemd timer that handles renewal automatically; you can confirm it's active with:

```bash
sudo systemctl status certbot.timer
```

Because Nginx is running natively on the host rather than in a container here, this is the standard Certbot flow with no extra volume-mounting or container gymnastics required.

## 7. GitHub Actions: Build, Push, Deploy

The workflow builds the image, pushes it to GHCR using the automatically-provided `GITHUB_TOKEN` (no separate registry credentials needed), then SSHes into the host to pull and restart. Store `EC2_SSH_KEY` and `EC2_HOST` as repository secrets.

```yaml
name: Build and Deploy Backend

on:
  push:
    branches: [main]

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v4

      - name: Log in to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ghcr.io/${{ github.repository_owner }}/tenanttable-api:latest

  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    steps:
      - name: Deploy over SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ubuntu
          key: ${{ secrets.EC2_SSH_KEY }}
          script: |
            cd /home/ubuntu/tenanttable
            docker compose pull
            docker compose up -d
            docker image prune -f
```

`docker compose pull` fetches the newly tagged image, `docker compose up -d` recreates the backend container only (since it's the only service and its image changed), and `docker image prune -f` removes the now-dangling previous image layer so the host's disk doesn't accumulate old versions across repeated deploys.

As with any deploy using a floating `:latest` tag, rollbacks are harder than they need to be since there's no record of exactly which build is currently running. Tagging each build with the Git SHA in addition to `latest` (`tags: | ghcr.io/.../tenanttable-api:latest, ghcr.io/.../tenanttable-api:${{ github.sha }}`) costs nothing extra and means a bad deploy can be rolled back by pointing the compose file at a specific known-good SHA rather than only having `latest` to work with.

## 8. Operational Notes

Logs are viewed through Docker rather than journalctl: `docker compose logs -f backend` to tail live, or `docker compose logs --tail=200 backend` for a recent snapshot. A plain restart without a new image just needs `docker compose restart backend`. Check host disk usage periodically with `docker system df`, since build cache and old layers accumulate over time even with the prune step in the deploy workflow. And as before, `ddl-auto: validate` means every entity change needs a corresponding migration script checked in and applied before that code ships, or the container will fail to start with a schema mismatch rather than silently altering the production table.
