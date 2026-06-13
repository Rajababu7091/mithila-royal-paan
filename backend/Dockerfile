# ============================================================
# MITHILA ROYAL PAAN - Production Dockerfile
# Build:   maven:3.9.6-eclipse-temurin-17
# Runtime: eclipse-temurin:17-jre-alpine
# Compatible with: Render, Railway
# ============================================================

# ── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first — downloads dependencies as a separate cached layer
# (only reruns when pom.xml changes, not on every source change)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code and build the fat JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

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
