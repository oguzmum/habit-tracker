package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.service.HabitService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;


import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.thymeleaf.context.Context;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/export")
public class ExportController {

    private final HabitService habitService;
    private final SpringTemplateEngine templateEngine;

    public ExportController(HabitService habitService, SpringTemplateEngine templateEngine) {
        this.habitService = habitService;
        this.templateEngine = templateEngine;
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

    @GetMapping(value = "/habits/month.pdf", produces = "application/pdf")
    public void exportMonthlyPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpServletResponse response
    ) throws Exception {

        LocalDate today = LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;
        if (m < 1 || m > 12) m = today.getMonthValue();

        LocalDate first = LocalDate.of(y, m, 1);
        int daysInMonth = first.lengthOfMonth();

        List<LocalDate> monthDays = new ArrayList<>(daysInMonth);
        for (int d = 1; d <= daysInMonth; d++) {
            monthDays.add(first.withDayOfMonth(d));
        }

        List<Habit> habits = habitService.findAll();
        Map<Long, Map<LocalDate, Boolean>> habitMonthStatus = new HashMap<>();
        for (Habit h : habits) {
            Map<LocalDate, Boolean> perDay = new HashMap<>();
            for (LocalDate d : monthDays) {
                perDay.put(d, habitService.isHabitDoneAtDate(h.getId(), d));
            }
            habitMonthStatus.put(h.getId(), perDay);
        }

        String headline = first.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));

        Context ctx = new Context(Locale.ENGLISH);
        ctx.setVariable("monthHeadline", headline);
        ctx.setVariable("monthDays", monthDays);
        ctx.setVariable("habits", habits);
        ctx.setVariable("habitMonthStatus", habitMonthStatus);
        ctx.setVariable("today", today);

        String html = templateEngine.process("export-month", ctx);

        String filename = String.format("habits-%d-%02d.pdf", y, m);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType("application/pdf");

        try (OutputStream os = response.getOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
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
