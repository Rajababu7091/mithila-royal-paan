# ============================================================
# MITHILA ROYAL PAAN - Production Dockerfile
# Java 17 (Eclipse Temurin) | Spring Boot 3.2.5 | Maven
# Compatible with: Render, Railway
# ============================================================

# ── Stage 1: Build ──────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and project descriptor first (layer cache)
COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml ./

# Make mvnw executable and download all dependencies (cached layer)
RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the fat JAR, skip tests
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/royalpaan-0.0.1-SNAPSHOT.jar app.jar

# Set ownership
RUN chown appuser:appgroup app.jar

USER appuser

# Expose Spring Boot default port
EXPOSE 8080

# JVM tuning for container environments (Render free tier is memory-constrained)
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
