# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:resolve -DskipTests
COPY src ./src
RUN mvn -B package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN mkdir -p data && apk add --no-cache curl
COPY --from=builder /workspace/target/ganttpro-app-*.jar app.jar
EXPOSE 8080
ENV PORT=8080
HEALTHCHECK --interval=10s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/login || exit 1
# Start app with minimal memory
CMD ["sh", "-c", "java -Xmx256m -Xms128m -XX:+UseG1GC -jar /app/app.jar"]
