## Possibilities to implement the Dockerfile

There are two possible ways to implement the Dockerfile:

1. **Compile the source code inside the Dockerfile**:
   Copy the entire source code into the Docker container and compile it with Maven to generate the `.jar` file.

2. **Build the `.jar` locally beforehand**:
   Build the `.jar` file locally using Maven, and then only copy the generated `.jar` into the Docker image.


3. Use a [Multi-Stage](https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/) Dockerfile
   - The Dockerfile is split into two stages:
      - Stage 1 (builder): uses Maven to compile the source code and package the `.jar`
      - Stage 2 (runtime): copies only the resulting `.jar` into a minimal JRE image and runs it

It basically is a mix of 1. and 2. :D. This way the Dockerimage still stays memory-efficient.

> At first I had the second approach as I didn't knew of multi-stage builds.
> 
> A colleague introduced me to multi-stage builds and for obvious reasons I changed my approach :D

---

## How to Create the Docker Image

### With multi-stage build
Just build the image via the Dockerfile. 
As I've moved the Dockerfile to a dedicated `Docker` Folder, make sure to execute the command from root, so the Dockerfile has the correct context in order to copy the `pom.xml` and `src` folder

```bash
docker build -f Docker/Dockerfile -t habit-tracker .  
```

### Old (second) approach 
Use the `build-docker.sh` script.

What it basically does

#### 1. Build the `.jar` File Locally
```bash
mvn clean package
```
This will generate the `.jar` file in the `target/` directory of the project

#### 2. Build the Docker Image
```bash
docker build -t habit-tracker .
```