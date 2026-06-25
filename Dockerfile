# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:resolve -DskipTests
COPY src ./src
RUN mvn -B package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p data
COPY --from=builder /workspace/target/ganttpro-app-*.jar app.jar
EXPOSE 8080
CMD ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
