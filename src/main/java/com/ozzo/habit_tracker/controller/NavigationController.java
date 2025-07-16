package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.model.Habit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
public class NavigationController {

    /*
    * INFO: i always have to return index and not the requested page itself
    * otherwise the page isnt loaded in the main area, but the html page itself opens
    * */

    private HabitController habitController = new HabitController();

    //instead of creating habits in specific calls, load it once and make it available everwhere
    @ModelAttribute("habits")
    List<Habit> habits() {
        return habitController.getAllHabits();
    }

//    @GetMapping({"/", "/daily", "/home"}) its possible to define multiple :D
    @GetMapping("/")
    public String showDaily(Model model) {
        model.addAttribute("newPage", "daily");
        return "index";
    }

    @GetMapping("/goals")
    public String showGoals(Model model) {
        model.addAttribute("newPage", "goals");
        return "index";
    }

    // i couldn't create the text outputs in the frontend, so i create them here and load dem in model Variables
    @GetMapping("/week")
    public String showWeek(Model model) {
        LocalDate today   = LocalDate.now();
        LocalDate monday  = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday  = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // e.g. "Week 25: 01.07. – 07.07."
        String headline = String.format(
                "Week %d: %s - %s",
                today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
                monday.format(DateTimeFormatter.ofPattern("dd.MM.")),
                sunday.format(DateTimeFormatter.ofPattern("dd.MM."))
        );

        model.addAttribute("weekHeadline", headline);
        model.addAttribute("days", List.of("Mon","Tue","Wed","Thu","Fri","Sat","Sun"));
        model.addAttribute("newPage", "week");
        return "index";
    }
}
