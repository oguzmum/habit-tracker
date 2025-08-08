## Possibilities to implement the Dockerfile

There are two possible ways to implement the Dockerfile:

1. **Compile the source code inside the Dockerfile**:
   Copy the entire source code into the Docker container and compile it with Maven to generate the `.jar` file.

2. **Build the `.jar` locally beforehand**:
   Build the `.jar` file locally using Maven, and then only copy the generated `.jar` into the Docker image.

I will go with the second approach as it keeps the Docker image memory-efficient and avoids the overhead of building inside the container.

---

## How to Create the Docker Image

### 1. Build the `.jar` File Locally
Use the following Maven command to build the `.jar` file locally:
```bash
mvn clean package
```

This will generate the `.jar` file in the `target/` directory of your project.

### 2. Build the Docker Image

Next, create the Docker image by running the following command in the root directory where your Dockerfile is located:
```bash
docker build -t habit-tracker .
```

See build-docker.sh for how its implemented for this project specifically