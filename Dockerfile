# Многоэтапный Dockerfile для GanttPRO
# Этап 1: Сборка
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -q

# Этап 2: Запуск
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/ganttpro-app-1.0.0.jar ganttpro-app-1.0.0.jar
EXPOSE 8080
ENV JAVA_OPTS="-Dserver.port=8080"
CMD ["java", "-jar", "ganttpro-app-1.0.0.jar"]
