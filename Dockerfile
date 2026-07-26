# ---- Stage 1: Build ----------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copy the mvnw wrapper with executable permissions.
# --chmod=0755 Sets permissions (Owner: read/write/execute | Group/Others: read/execute)
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

# Copy only the POM first so dependency layers are cached separately
# from source code changes (faster rebuilds).
COPY pom.xml .
RUN ./mvnw -B dependency:go-offline

# Now copy the rest of the source and build.
COPY src ./src
RUN ./mvnw -B clean package -DskipTests


# ---- Stage 2: Run -------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS run

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]