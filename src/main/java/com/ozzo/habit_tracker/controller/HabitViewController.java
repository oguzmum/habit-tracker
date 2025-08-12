package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.service.CategoryService;
import com.ozzo.habit_tracker.service.GoalService;
import com.ozzo.habit_tracker.service.HabitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HabitViewController {

    private static final Logger log = LoggerFactory.getLogger(HabitViewController.class);

    /*
     * INFO: i always have to return index and not the requested page itself
     * otherwise the page isnt loaded in the main area, but the html page itself opens
     * */

    private final HabitService habitService;
    private final GoalService goalService;
    private final CategoryService categoryService;

    public HabitViewController(HabitService habitService, GoalService goalService, CategoryService categoryService) {
        this.habitService = habitService;
        this.goalService = goalService;
        this.categoryService = categoryService;
    }

    @GetMapping("/habits")
    public String showHabits(Model model) {
        List<Habit> habits = habitService.findAll();
        model.addAttribute("habits", habits);
        model.addAttribute("newPage", "habits");

        return "index";
    }

    @GetMapping("/habits/new-page")
    public String showNewHabitForm(Model model) {
        //new Habit that will created with parameters as its being defined by the user in the UI :D
        model.addAttribute("habit", new Habit());
        model.addAttribute("goals", goalService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("newPage", "addNewHabit");
        return "index";
    }

    @PostMapping("/habits-form")
    public String saveHabit(@ModelAttribute("habit") Habit habit) {
        habitService.save(habit);
        //i have to use redirect, as i dont want to load the habits.html page on its on
        //but load the html in the main area of the index.html
        return "redirect:/habits";
    }

    @GetMapping({"/", "/today"})
    public String showDaily(Model model, @RequestParam(value = "date", required = false) String dateParam) {
        LocalDate today = LocalDate.now();
        LocalDate current;

        if (dateParam != null && !dateParam.isBlank()) {
            try {
                current = LocalDate.parse(dateParam);
            } catch (DateTimeParseException e) {
                current = today;
            }
        } else {
            current = today;
        }

        // don't allow future dates
        if (current.isAfter(today)) {
            return "redirect:/daily?date=" + today;
        }

        LocalDate prevDate = current.minusDays(1);
        LocalDate nextCandidate = current.plusDays(1);
        LocalDate nextDate = nextCandidate.isAfter(today) ? today : nextCandidate;
        boolean nextDisabled = !current.isBefore(today);

        List<Habit> habits = habitService.findAll();
        Map<Long, Boolean> habitStatus = new HashMap<>();
        for (Habit h : habits) {
            habitStatus.put(h.getId(), habitService.isHabitDoneAtDate(h.getId(), current));
        }

        model.addAttribute("habits", habits);
        model.addAttribute("habitDayStatus", habitStatus);
        model.addAttribute("currentDate", current);
        model.addAttribute("prevDate", prevDate);
        model.addAttribute("nextDate", nextDate);
        model.addAttribute("nextDisabled", nextDisabled);
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

    @GetMapping("/habits/{id}")
    public String showGoalDetails(@PathVariable Long id, Model model) {
        Habit habit = habitService.findById(id);
        model.addAttribute("habit", habit);
        model.addAttribute("newPage", "detailsHabit");
        return "index";
    }

    @PostMapping("/habits/delete/{id}")
    public String deleteGoal(@PathVariable Long id) {
        habitService.deleteById(id);
        return "redirect:/habits";
    }


}
