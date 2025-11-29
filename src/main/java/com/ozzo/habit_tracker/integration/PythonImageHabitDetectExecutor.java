package com.ozzo.habit_tracker.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class PythonImageHabitDetectExecutor {

    private final String pythonExecutable; //the bin/python inside the virtual environment
    private final String scriptPath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PythonImageHabitDetectExecutor(String pythonExecutable, String scriptPath) {
        this.pythonExecutable = pythonExecutable;
        this.scriptPath = scriptPath;
    }

    public List<List<Boolean>> detectHabits(Path imagePath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                pythonExecutable,
                scriptPath,
                imagePath.toString()
        );

        // Optional: set working directory
        // pb.directory(new File("/Users/oguzm1/Documents/02-Projekte/SW Projekte/habit-tracker/scripts"));

        // merge STDERR in STDOUT
        pb.redirectErrorStream(true);

        Process process = pb.start();
        // read the output
        String json;
        try (InputStream is = process.getInputStream()) {
            json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python process exited with code " + exitCode + ". Output: " + json);
        }

        // JSON -> Map -> Matrix
        Map<String, Object> result = objectMapper.readValue(json, new TypeReference<>() {});
        @SuppressWarnings("unchecked")
        List<List<Boolean>> matrix = (List<List<Boolean>>) result.get("matrix");
        return matrix;
    }
}
