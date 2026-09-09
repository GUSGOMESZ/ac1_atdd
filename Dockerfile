# Build Stage
FROM maven:3.9.0-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:resolve

COPY src ./src

RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# AQUI: Use o nome correto do JAR
COPY --from=builder /build/target/ac1_atdd-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]