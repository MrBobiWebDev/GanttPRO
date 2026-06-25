# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"
CMD exec java $JAVA_OPTS -jar app.jar
