
# syntax=docker/dockerfile:1

# =========================================================
# STAGE 1: BUILD WAR WITH MAVEN + JAVA 21
# =========================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy pom.xml first to optimize Docker layer caching
COPY pom.xml .

# Download dependencies
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline

# Copy application source code
COPY src ./src

# Build the WAR file
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests

# =========================================================
# STAGE 2: RUN WAR ON TOMCAT 11 + JAVA 21
# =========================================================
FROM tomcat:11.0-jdk21-temurin

# Remove Tomcat default applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy the application as ROOT
# URL: http://localhost:8080/
COPY --from=build /build/target/fishingshop.war \
    /usr/local/tomcat/webapps/ROOT.war

# Create persistent upload directory
RUN mkdir -p /data/uploads

# Configure JVM
ENV JAVA_OPTS="--enable-preview -Dfile.encoding=UTF-8 -Dfishing.upload.dir=/data/uploads"

# Run as a non-root user and grant required permissions
RUN useradd -r -u 1001 -g root appuser \
    && chown -R 1001:0 /usr/local/tomcat /data \
    && chmod -R g+rwX /usr/local/tomcat /data

USER 1001

# Persist uploaded product images
VOLUME ["/data/uploads"]

# Tomcat HTTP port
EXPOSE 8080

# Start Tomcat in the foreground
CMD ["catalina.sh", "run"]
