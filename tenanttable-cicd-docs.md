# TenantTable — CI/CD Documentation (Personal Notes)

Backend: Spring Boot (Java) + PostgreSQL
Registry: Docker Hub
CI: GitHub Actions
Deploy target: AWS EC2 (single instance, Docker Compose)

---

## 1. Pipeline Overview

```
push to main
   │
   ▼
GitHub Actions: build & test (Maven)
   │
   ▼
Build Docker image (multi-stage)
   │
   ▼
Push image to Docker Hub  (tags: <git-sha> + latest)
   │
   ▼
SSH into AWS EC2
   │
   ▼
docker compose pull + up -d (zero-downtime-ish restart)
```

Trigger: push to `main` only. No separate PR workflow — every push to `main` runs the full build → push → deploy chain.

---

## 2. Dockerfile (multi-stage)

Put this at the repo root of `tenanttable-api`.

```dockerfile
# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Run stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
```

Notes:
- `-DskipTests` here because tests already run as a separate CI step — don't run them twice.
- Adjust JDK version to match what TenantTable is actually built against.
- `UseContainerSupport` lets the JVM respect container memory limits (important on a small EC2 instance).

Build/test locally:
```bash
docker build -t tenanttable-api:local .
docker run -p 8080:8080 --env-file .env tenanttable-api:local
```

---

## 3. Docker Hub Setup

1. Create repo on Docker Hub: `<dockerhub-username>/tenanttable-api`
2. Generate **two separate access tokens** — never reuse one token across CI and the server:
   - **CI push token** (`Read & Write`) — used only by GitHub Actions to build and push images.
     Docker Hub → Account Settings → Security → New Access Token → scope: `Read & Write`
   - **Server pull token** (`Read-only`) — used only on the EC2 box to pull images. If the server is ever compromised, this token can't be used to push a tampered image.
     Docker Hub → Account Settings → Security → New Access Token → scope: `Read-only`
3. Add the push token to GitHub repo secrets (`Settings → Secrets and variables → Actions`):
   - `DOCKERHUB_USERNAME`
   - `DOCKERHUB_TOKEN` (Read & Write token)
4. Keep the pull token for the EC2 server — do **not** add it to GitHub secrets, since Actions never needs it (see 5.5).

---

## 4. GitHub Actions Workflow

`.github/workflows/deploy.yml`

```yaml
name: CI/CD - TenantTable API

on:
  push:
    branches: [main]

env:
  IMAGE_NAME: ${{ secrets.DOCKERHUB_USERNAME }}/tenanttable-api

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Run tests
        run: mvn -B test

      - name: Build jar
        run: mvn -B clean package -DskipTests

  push-image:
    needs: build-and-test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Log in to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Build and push
        uses: docker/build-push-action@v6
        with:
          context: .
          push: true
          tags: |
            ${{ env.IMAGE_NAME }}:latest
            ${{ env.IMAGE_NAME }}:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

  deploy:
    needs: push-image
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to AWS EC2
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.AWS_EC2_HOST }}
          username: ${{ secrets.AWS_EC2_USER }}
          key: ${{ secrets.AWS_EC2_SSH_KEY }}
          script: |
            cd ~/tenanttable
            docker compose pull api
            docker compose up -d api
            docker image prune -f
```

Secrets needed in GitHub (`Settings → Secrets and variables → Actions`):

| Secret | Value |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub username |
| `DOCKERHUB_TOKEN` | Docker Hub access token |
| `AWS_EC2_HOST` | EC2 public IP or Elastic IP |
| `AWS_EC2_USER` | usually `ubuntu` (Ubuntu AMI) or `ec2-user` (Amazon Linux) |
| `AWS_EC2_SSH_KEY` | contents of your `.pem` private key |

---

## 5. AWS EC2 Setup (one-time)

### 5.1 Instance
- Launch a `t3.micro` or `t3.small` (free tier eligible is `t2.micro`/`t3.micro` depending on account age)
- Security group inbound rules:
  - 22 (SSH) — restrict to your IP if possible
  - 80/443 (HTTP/HTTPS) — if using Nginx reverse proxy
  - 8080 — only if exposing the API directly without a reverse proxy (not recommended for prod)

### 5.2 Install Docker on EC2
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
# log out/in for group change to take effect
```

### 5.3 Log in on the server with the read-only pull token

Do this once, interactively, when you first set up the box (or re-run it if the token is rotated). This is the `Read-only` token from step 3, not the CI one.

```bash
echo '<read-only-token>' | docker login -u <dockerhub-username> --password-stdin
```

Docker caches this in `~/.docker/config.json`, so `docker compose pull` will use it automatically going forward — the GitHub Actions deploy step doesn't need to pass any Docker Hub credentials at all, it just runs `docker compose pull` over SSH and the server's own cached login handles auth.

### 5.4 `docker-compose.yml` on the server (`~/tenanttable/docker-compose.yml`)

```yaml
services:
  api:
    image: <dockerhub-username>/tenanttable-api:latest
    restart: unless-stopped
    ports:
      - "8080:8080"
    env_file:
      - .env
    depends_on:
      - db

  db:
    image: postgres:16
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "127.0.0.1:5432:5432"   # not exposed publicly

volumes:
  pgdata:
```

`.env` lives only on the server, never in the repo:
```
DB_NAME=tenanttable
DB_USER=tenanttable_user
DB_PASSWORD=<strong-password>
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<secret>
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/tenanttable
```

If using **RDS instead of a containerized Postgres**: drop the `db` service, point `SPRING_DATASOURCE_URL` at the RDS endpoint, and put the RDS security group behind the EC2 instance's security group only.

### 5.5 Nginx + HTTPS (optional but recommended)
```bash
sudo apt install -y nginx certbot python3-certbot-nginx
```
Nginx reverse proxy config (`/etc/nginx/sites-available/tenanttable`):
```nginx
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
```bash
sudo ln -s /etc/nginx/sites-available/tenanttable /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d api.yourdomain.com
```

---

## 6. Personal Cheat Sheet

### Docker
```bash
# Build & tag manually
docker build -t <user>/tenanttable-api:latest .
docker push <user>/tenanttable-api:latest

# See what's running
docker ps
docker logs -f tenanttable-api-api-1

# Shell into a running container
docker exec -it tenanttable-api-api-1 sh

# Clean up dangling images/containers (do this periodically on the t3.micro — disk fills fast)
docker system prune -af --volumes   # careful: -f volumes wipes unused volumes too, check first

# Force pull latest even if tag unchanged locally
docker compose pull api && docker compose up -d --force-recreate api
```

### GitHub Actions debugging
```bash
# Re-run a failed workflow from the GitHub UI, or via CLI:
gh run list --workflow=deploy.yml
gh run rerun <run-id>
gh run view <run-id> --log
```

### Manual deploy (if Actions is broken and you need to ship now)
```bash
ssh -i tenanttable.pem ubuntu@<ec2-ip>
cd ~/tenanttable
docker compose pull api
docker compose up -d api
docker logs -f tenanttable-api-api-1   # watch it come up
```

### Rollback
```bash
# On the server — pin to a specific known-good SHA tag instead of :latest
docker pull <user>/tenanttable-api:<good-sha>
docker tag <user>/tenanttable-api:<good-sha> <user>/tenanttable-api:latest
docker compose up -d api
```

### Quick health check after deploy
```bash
curl -s https://api.yourdomain.com/actuator/health
```
(assuming Spring Boot Actuator is enabled — add `spring-boot-starter-actuator` if not already there)

### Common gotchas
- **`Cannot connect to db`** right after deploy → Postgres container took longer to start than the API; add a `depends_on` healthcheck or a retry/backoff in the app's datasource config.
- **EC2 disk full** → almost always old Docker images. `docker system prune -af` (check `docker images` first).
- **SSH action fails silently** → check the EC2 security group actually allows the GitHub Actions runner's IP range on port 22 (GitHub doesn't use static IPs — either allow `0.0.0.0/0` on 22 with key-only auth, or use AWS Systems Manager Session Manager instead of raw SSH for tighter security).
- **New env var not picked up** → `docker compose up -d` alone won't recreate the container if the image tag is unchanged; use `--force-recreate` or bump the tag.

---

## 7. Possible Next Steps (not yet implemented)
- Move from `latest`-tag deploys to immutable SHA-tag deploys for easier rollback.
- Add a staging environment (separate EC2 instance or separate compose project) before prod.
- Migrate from single-EC2 + Docker Compose to ECS/Fargate once traffic justifies it — avoids managing the host OS at all.
- Add Slack/Discord webhook notification on deploy success/failure in the Actions workflow.
