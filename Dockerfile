# syntax=docker/dockerfile:1.7

# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 1. Copy just enough to resolve dependencies (cached layer)
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw \
 && ./mvnw dependency:go-offline -B

# 2. Now copy source and build the fat jar
COPY src src
RUN ./mvnw clean package -B -DskipTests \
 && cp target/*-SNAPSHOT.jar app.jar

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as a non-root user (security best practice)
RUN useradd -r -u 1001 -m -d /home/app spring
USER spring

# Copy the fat jar from the build stage
COPY --from=build --chown=spring:spring /app/app.jar app.jar

# Render / Cloud Run / most PaaS inject PORT; default to 8080 for local `docker run`
ENV PORT=8080
EXPOSE 8080

# JVM flags:
#  - UseContainerSupport: honor cgroup memory limits (default since JDK 10 but explicit is safer)
#  - MaxRAMPercentage: use up to ~75% of the container's RAM for the heap
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50"

ENTRYPOINT ["sh","-c","exec java $JAVA_TOOL_OPTIONS -jar app.jar --server.port=$PORT"]
