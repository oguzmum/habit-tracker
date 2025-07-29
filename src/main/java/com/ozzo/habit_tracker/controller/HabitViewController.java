package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.service.HabitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HabitViewController {

    /*
     * INFO: i always have to return index and not the requested page itself
     * otherwise the page isnt loaded in the main area, but the html page itself opens
     * */

    private final HabitService habitService;

    public HabitViewController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping("/habits")
    public String showHabits(Model model) {
        List<Habit> habits = habitService.findAll();
        model.addAttribute("habits", habits);
        model.addAttribute("newPage", "habits");

        return "index";
    }

    @GetMapping("/habits/new-page")
    public String showNewHabitDialog(Model model) {
        model.addAttribute("newPage", "addNewHabit");
        return "index";
    }

    @GetMapping({"/", "/today"})
    public String showDaily(Model model) {
        LocalDate today = LocalDate.now();

        List<Habit> habits = habitService.findAll();
        Map<Long, Boolean> habitStatus = new HashMap<>();
        for (Habit h : habits) {
            habitStatus.put(h.getId(), habitService.isHabitDoneAtDate(h.getId(), today));
        }

        model.addAttribute("habits", habits);
        model.addAttribute("habitDayStatus", habitStatus);
        model.addAttribute("newPage", "daily");

        return "index";
    }

    // i couldn't create the text outputs in the frontend, so i create them here and load dem in model Variables
    @GetMapping("/week")
    public String showWeek(Model model) {
        LocalDate today   = LocalDate.now();
        LocalDate monday  = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDays.add(monday.plusDays(i));
        }

        // e.g. "Week 25: 01.07. – 07.07."
        String headline = String.format(
                "Week %d: %s - %s",
                today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
                weekDays.getFirst().format(DateTimeFormatter.ofPattern("dd.MM.")),
                weekDays.getLast().format(DateTimeFormatter.ofPattern("dd.MM."))
        );

        model.addAttribute("weekHeadline", headline);
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("newPage", "week");

        List<Habit> habits = habitService.findAll();
        model.addAttribute("habits", habits);

        //get which habits are (un)checked for this week
        Map<Long, Map<LocalDate, Boolean>> habitWeekStatus = new HashMap<>();
        for (Habit h : habits) {
            Map<LocalDate, Boolean> perDay = new HashMap<>();
            for (LocalDate d : weekDays) {
                perDay.put(d, habitService.isHabitDoneAtDate(h.getId(), d));
            }
            habitWeekStatus.put(h.getId(), perDay);
        }
        model.addAttribute("habitWeekStatus", habitWeekStatus);

        return "index";
    }


}
