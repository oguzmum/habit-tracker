# image for building the app
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#image for runtime
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Python runtime for image detection
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        python3 python3-venv python3-pip \
        libgl1 libglib2.0-0 libheif1 \
    && rm -rf /var/lib/apt/lists/* \
    && python3 -m venv /opt/venv

RUN /opt/venv/bin/pip install --no-cache-dir --upgrade pip \
    && /opt/venv/bin/pip install --no-cache-dir \
        opencv-python-headless \
        numpy \
        pillow \
        pyheif

COPY --from=builder /app/target/*.jar habit-tracker.jar
COPY scripts ./scripts

ENV IMAGE_UPLOAD_DIR=/app/uploads \
    IMAGE_DETECT_PYTHON_PATH=/opt/venv/bin/python \
    IMAGE_DETECT_SCRIPT_PATH=/app/scripts/habitDetect.py

RUN mkdir -p /app/uploads

ENTRYPOINT ["java", "-jar", "habit-tracker.jar"]