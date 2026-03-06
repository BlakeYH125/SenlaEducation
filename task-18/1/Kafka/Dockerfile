FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY producer/pom.xml producer/
COPY consumer/pom.xml consumer/

RUN mvn dependency:go-offline -B

COPY producer/src producer/src
COPY consumer/src consumer/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS producer-image
WORKDIR /app
COPY --from=builder /app/producer/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS consumer-image
WORKDIR /app
COPY --from=builder /app/consumer/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]