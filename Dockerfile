# Многоэтапный Dockerfile для GanttPRO
# Этап 1: Сборка
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Этап 2: Запуск
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/ganttpro-app-1.0.0.jar app.jar
EXPOSE 8080
ENV PORT=8080
CMD ["java", "-jar", "app.jar"]
