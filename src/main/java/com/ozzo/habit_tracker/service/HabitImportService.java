package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.dto.HabitImportForm;
import com.ozzo.habit_tracker.dto.HabitImportRow;
import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.entity.UploadedImage;
import com.ozzo.habit_tracker.integration.PythonImageHabitDetectExecutor;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HabitImportService {

    private static final Logger log = LoggerFactory.getLogger(HabitImportService.class);

    private final HabitService habitService;
    private final UploadedImageService uploadedImageService;
    private final PythonImageHabitDetectExecutor pythonExecutor;
    private final String pythonExecutable;
    private final String scriptPath;

    public HabitImportService(
            HabitService habitService,
            UploadedImageService uploadedImageService,
            @Value("${app.image-detect.python-path:}") String pythonExecutable,
            @Value("${app.image-detect.script-path:}") String scriptPath) {
        this.habitService = habitService;
        this.uploadedImageService = uploadedImageService;
        this.pythonExecutable = pythonExecutable;
        this.scriptPath = scriptPath;
        this.pythonExecutor = new PythonImageHabitDetectExecutor(pythonExecutable, scriptPath);
    }

    // the function to execute the python image processing script, extract the json message and build the table that can be edited before persisting habitentries
    public HabitImportForm buildImportForm(Long uploadedImageId, YearMonth targetMonth) throws IOException, InterruptedException {
        ensureConfigured();

        UploadedImage uploadedImage = uploadedImageService.getById(uploadedImageId);
        if (uploadedImage == null) {
            throw new IllegalArgumentException("Could not find the selected image.");
        }

        List<List<Boolean>> detectedMatrix = pythonExecutor.detectHabits(Path.of(uploadedImage.getStoragePath()));
        List<Habit> orderedHabits = habitService.findHabitsByPredefinedOrderForImageProcessedTable();
        int daysInMonth = targetMonth.lengthOfMonth();

        List<HabitImportRow> rows = new ArrayList<>();
        for (int i = 0; i < orderedHabits.size(); i++) {
            Habit habit = orderedHabits.get(i);
            List<Boolean> detectedRow = i < detectedMatrix.size() ? detectedMatrix.get(i) : Collections.emptyList();

            List<Boolean> days = new ArrayList<>(daysInMonth);
            for (int d = 0; d < daysInMonth; d++) {
                boolean value = d < detectedRow.size() && Boolean.TRUE.equals(detectedRow.get(d));
                days.add(value);
            }

            HabitImportRow row = new HabitImportRow();
            row.setHabitId(habit.getId());
            row.setHabitName(habit.getName());
            row.setDays(days);
            rows.add(row);
        }

        HabitImportForm form = new HabitImportForm();
        form.setUploadedImageId(uploadedImage.getId());
        form.setUploadedImageName(uploadedImage.getOriginalFilename());
        form.setYear(targetMonth.getYear());
        form.setMonth(targetMonth.getMonthValue());
        form.setRows(rows);

        log.info(
            "Prepared import form for image {} for {} with {} habits",
            uploadedImage.getOriginalFilename(),
            targetMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            rows.size()
        );

        return form;
    }

    public List<LocalDate> getMonthDays(YearMonth yearMonth) {
        List<LocalDate> days = new ArrayList<>(yearMonth.lengthOfMonth());
        for (int d = 1; d <= yearMonth.lengthOfMonth(); d++) {
            days.add(yearMonth.atDay(d));
        }
        return days;
    }

    public void persistImport(HabitImportForm form) {
        YearMonth targetMonth = YearMonth.of(form.getYear(), form.getMonth());
        for (HabitImportRow row : form.getRows()) {
            for (int i = 0; i < row.getDays().size(); i++) {
                LocalDate date = targetMonth.atDay(i + 1);
                boolean completed = Boolean.TRUE.equals(row.getDays().get(i));
                if (completed) {
                    habitService.markHabitAsDone(row.getHabitId(), date);
                } else {
                    habitService.markHabitAsUndone(row.getHabitId(), date);
                }
            }
        }
    }

    private void ensureConfigured() {
        if (pythonExecutable == null || pythonExecutable.isBlank() || scriptPath == null || scriptPath.isBlank()) {
            throw new IllegalStateException(
                "Image detection is not configured. Please set app.image-detect.python-path and "
                        + "app.image-detect.script-path properties.");
        }
    }
}
