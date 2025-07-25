package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.service.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private static final Logger logger = LoggerFactory.getLogger(HabitController.class);

    private final HabitService habitService;

    public HabitController(HabitService habitService){ this.habitService = habitService; }

    @PostMapping("/toggle-day")
    @ResponseBody
    public ResponseEntity<Void> toggleHabitDay(
            @RequestParam Integer habitId,
            @RequestParam Boolean completed) {

        LocalDate today = LocalDate.now();
        if (completed) {
            habitService.markHabitAsDone(habitId, today);
        } else {
            habitService.markHabitAsUndone(habitId, today);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle-week")
    @ResponseBody
    public ResponseEntity<Void> toggleHabitWeek(
            @RequestParam Integer habitId,
            @RequestParam("date") String dateStr,
            @RequestParam Boolean completed) {

        LocalDate date = LocalDate.parse(dateStr);

        if (completed) {
            habitService.markHabitAsDone(habitId, date);
        } else {
            habitService.markHabitAsUndone(habitId, date);
        }
        return ResponseEntity.ok().build();
    }

}
