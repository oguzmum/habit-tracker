# image for building the app
FROM maven:3.9.9-eclipse-temurin-24 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#image for runtime
FROM openjdk:24-jdk-slim AS runtime
WORKDIR /app

COPY --from=builder /app/target/*.jar habit-tracker.jar
ENTRYPOINT ["java", "-jar", "habit-tracker.jar"]
# CMD ["java", "-jar", "habit-tracker.jar"]