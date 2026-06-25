FROM maven:3.9-eclipse-temurin-17
WORKDIR /app
RUN mkdir -p /app/data

# Copy and build
COPY pom.xml .
RUN mvn dependency:resolve -DskipTests -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

EXPOSE 8080
ENV PORT=8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/login || exit 1

CMD ["sh", "-c", "java -Xmx256m -Xms128m -XX:+UseG1GC -Dspring.profiles.active=railway -jar target/ganttpro-app-1.0.0.jar"]
