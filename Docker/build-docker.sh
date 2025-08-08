#!/bin/bash

cd ~/Projekte/SW\ Projekte/habit-tracker

mvn clean package

JAR_NAME=$(ls target/*.jar | grep habit-tracker.*.jar | head -n 1)
echo "JAR_NAME: $JAR_NAME"

# as i firt change in to the main folder directory for this project to build the jar
# i have to change the Context when building the DOckerimage via Dockerfile
# alternatively i could cahneg the directory here to the Docker path, but that didn't work
# i guess because the buikld copntext was still in the root directory of the project then and not in th root/Docker
docker build -f Docker/Dockerfile --build-arg JAR_FILE=$(basename $JAR_NAME) -t habit-tracker .