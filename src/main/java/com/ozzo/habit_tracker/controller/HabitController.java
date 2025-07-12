package com.ozzo.habit_tracker.controller;

import org.springframework.ui.Model;
import com.ozzo.habit_tracker.model.Habit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
//this is liek the base route to be matched for all the below GetMapping routes
//e.g. for getAllHabits the request must be /habits/all
@RequestMapping("/habits")
public class HabitController {

    private final List<Habit> dummyHabitData = new ArrayList<>();

    // Beim Konstruktorstart ein paar Demo-Daten anlegen
    public HabitController() {
        dummyHabitData.add(new Habit(1, "10 000 Schritte", true));
        dummyHabitData.add(new Habit(2, "Trainieren", true));
        dummyHabitData.add(new Habit(3, "Lesen", false));
    }

    public List<Habit> getAllHabits(Model model) {
        model.addAttribute("habits", dummyHabitData);
        return dummyHabitData;
    }
}
