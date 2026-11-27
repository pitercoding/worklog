# syntax=docker/dockerfile:1

# ---- Build stage: compile and package the application ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Download dependencies first so Docker can cache this layer until the pom changes.
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw --batch-mode --no-transfer-progress dependency:go-offline

COPY src src
RUN ./mvnw --batch-mode --no-transfer-progress package -Dmaven.test.skip=true \
    && mv target/backend-*.jar target/app.jar

# ---- Runtime stage: small JRE image, runs as a non-root user ----
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app

COPY --from=build /workspace/target/app.jar app.jar

# Platforms like Render set PORT; the application reads it (server.port=${PORT:8080}).
ENV PORT=8080
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -q -O /dev/null "http://localhost:${PORT}/health" || exit 1

# Use at most 75% of the container memory for the heap (the free tiers are small).
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
