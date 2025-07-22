package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.service.HabitService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
//this is liek the base route to be matched for all the below GetMapping routes
//e.g. for getAllHabits the request must be /habits/all
@RequestMapping("/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService){ this.habitService = habitService; }

    @PostMapping("/done")
    @ResponseBody
    public void markHabitDone(@RequestParam("habitId") Integer id) {
        System.out.println("Received habitId: " + id);
        habitService.markHabitAsDone(id, LocalDate.now());
    }

    @PostMapping("/done-week")
    public void markHabitDone(@RequestParam("habitId") Integer id,
                              @RequestParam("date") String dateStr) {

        LocalDate date = (dateStr != null)
                ? LocalDate.parse(dateStr)
                : LocalDate.now();

        System.out.println("Habit " + id + " marked as done at " + date);
        habitService.markHabitAsDone(id, date);
    }

}
