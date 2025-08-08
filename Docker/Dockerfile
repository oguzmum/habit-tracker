FROM openjdk:24-jdk-slim

WORKDIR /app

ARG JAR_FILE
COPY ../target/${JAR_FILE} /app/habit-tracker.jar

CMD ["java", "-jar", "habit-tracker.jar"]

EXPOSE 1200