package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.service.HabitService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/export")
public class ExportController {

    private final HabitService habitService;

    public ExportController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping(value = "/habits/month.csv", produces = "text/csv")
    public void exportMonthlyCsv(@RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) Integer month,
                                 @RequestParam(defaultValue = ";") String delimiter,
                                 HttpServletResponse response)
            throws IOException {

        LocalDate now = LocalDate.now();
        int y = (year == null) ? now.getYear() : year;
        int m = (month == null) ? now.getMonthValue() : month;

        if (m < 1 || m > 12) {
            m = now.getMonthValue();
        }

        LocalDate first = LocalDate.of(y, m, 1);
        int daysInMonth = first.lengthOfMonth();

        String filename = String.format("habits-%d-%02d.csv", y, m);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType("text/csv; charset=UTF-8");

        OutputStream out = response.getOutputStream();

        // UTF-8 BOM helps Excel open UTF-8 CSV correctly
        out.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});

        try (Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            // Header
            w.write(quote("Habit", delimiter));
            for (int d = 1; d <= daysInMonth; d++) {
                w.write(delimiter);
                w.write(Integer.toString(d));
            }
            w.write("\n");

            // Rows
            List<Habit> habits = habitService.findAll();
            for (Habit h : habits) {
                w.write(quote(h.getName(), delimiter));
                for (int d = 1; d <= first.lengthOfMonth(); d++) {
                    LocalDate date = first.withDayOfMonth(d);
                    boolean done = habitService.isHabitDoneAtDate(h.getId(), date);
                    w.write(delimiter);
                    w.write(formatCell(done));
                }
                w.write("\n");
            }
            w.flush();
        }
    }

    private static String quote(String s, String delimiter) {
        if (s == null) return "";
        boolean needs = s.contains(delimiter) || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String t = s.replace("\"", "\"\"");
        return needs ? "\"" + t + "\"" : t;
    }

    private static String formatCell(boolean done) {
            return done ? "X" : "";
    }
}
