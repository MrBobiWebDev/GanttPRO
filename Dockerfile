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
RUN mkdir -p data
COPY --from=builder /workspace/target/ganttpro-app-*.jar app.jar
EXPOSE 8080
ENV PORT=8080
# Start app with minimal memory and diagnostic output
CMD ["sh", "-c", "echo 'Starting GanttPRO...' && java -Xmx256m -Xms128m -XX:+UseG1GC -jar /app/app.jar 2>&1"]
