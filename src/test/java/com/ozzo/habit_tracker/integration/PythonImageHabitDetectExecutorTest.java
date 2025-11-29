package com.ozzo.habit_tracker.integration;

import java.nio.file.Path;
import java.util.List;

public class PythonImageHabitDetectExecutorTest {

    public static void main(String[] args) {
        try {
            Path imagePath = Path.of("/Users/oguzm1/Documents/02-Projekte/SW Projekte/habit-tracker/uploads/IMG_2935-copy.png");

            PythonImageHabitDetectExecutor executor =
                    new PythonImageHabitDetectExecutor(
                            "/Users/oguzm1/Documents/02-Projekte/SW Projekte/habit-tracker/scripts/.venv311/bin/python",
                            "/Users/oguzm1/Documents/02-Projekte/SW Projekte/habit-tracker/scripts/habitDetect.py"
                    );

            List<List<Boolean>> matrix = executor.detectHabits(imagePath);

            System.out.println("Detected habit matrix:\n");
            for (int r = 0; r < matrix.size(); r++) {
                System.out.printf("Row %02d: ", r + 1);
                for (int c = 0; c < matrix.get(r).size(); c++) {
                    System.out.print(matrix.get(r).get(c) ? "X " : ". ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Error during test execution:");
            e.printStackTrace();
        }
    }
}
