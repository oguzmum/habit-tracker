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
import java.util.*;

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

    // i couldn't create the text outputs in the frontend, so i create them here and load them in model Variables
    @GetMapping("/week")
    public String showWeek(Model model, @RequestParam(value = "date", required = false) String dateParam) {
        LocalDate today   = LocalDate.now();

        LocalDate anchor;
        try {
            anchor = (dateParam == null || dateParam.isBlank()) ? today : LocalDate.parse(dateParam);
        } catch (DateTimeParseException e) {
            anchor = today;
        }
        LocalDate thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monday = anchor.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // never allow navigating beyond the current week
        if (monday.isAfter(thisMonday)) {
            return "redirect:/week?date=" + today;
        }

        LocalDate prevWeekDate = monday.minusWeeks(1);
        LocalDate nextWeekCandidate = monday.plusWeeks(1);
        LocalDate nextWeekDate = nextWeekCandidate.isAfter(thisMonday) ? thisMonday : nextWeekCandidate;
        boolean nextDisabled = monday.equals(thisMonday);


        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDays.add(monday.plusDays(i));
        }

        // e.g. "Week 25: 01.07. – 07.07."
        String headline = String.format(
                "Week %d: %s - %s",
                monday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR), //monday to get the correct weeknumber :D
                weekDays.getFirst().format(DateTimeFormatter.ofPattern("dd.MM.")),
                weekDays.getLast().format(DateTimeFormatter.ofPattern("dd.MM."))
        );

        model.addAttribute("today", today);
        model.addAttribute("weekHeadline", headline);
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("prevWeekDate", prevWeekDate);
        model.addAttribute("nextWeekDate", nextWeekDate);
        model.addAttribute("nextDisabled", nextDisabled);
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

    @GetMapping("/month")
    public String showMonth(Model model, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        LocalDate today = LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;
        if (m < 1 || m > 12) m = today.getMonthValue();

        LocalDate first = LocalDate.of(y, m, 1);
        LocalDate thisMonthFirst = LocalDate.of(today.getYear(), today.getMonthValue(), 1);

        // never navigate beyond current month
        if (first.isAfter(thisMonthFirst)) {
            return "redirect:/month?year=" + today.getYear() + "&month=" + today.getMonthValue();
        }

        LocalDate prevMonthFirst = first.minusMonths(1).withDayOfMonth(1);
        LocalDate nextCandidate  = first.plusMonths(1).withDayOfMonth(1);
        LocalDate nextMonthFirst = nextCandidate.isAfter(thisMonthFirst) ? thisMonthFirst : nextCandidate;
        boolean nextDisabled = first.equals(thisMonthFirst);

        // days in month
        List<LocalDate> monthDays = new ArrayList<>();
        for (int d = 1; d <= first.lengthOfMonth(); d++) {
            monthDays.add(first.withDayOfMonth(d));
        }

        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        String headline = first.format(monthFmt);

        List<Habit> habits = habitService.findAll();
        Map<Long, Map<LocalDate, Boolean>> habitMonthStatus = new HashMap<>();
        for (Habit h : habits) {
            Map<LocalDate, Boolean> perDay = new HashMap<>();
            for (LocalDate d : monthDays) {
                perDay.put(d, habitService.isHabitDoneAtDate(h.getId(), d));
            }
            habitMonthStatus.put(h.getId(), perDay);
        }

        model.addAttribute("monthHeadline", headline);
        model.addAttribute("monthDays", monthDays);
        model.addAttribute("prevMonthFirst", prevMonthFirst);
        model.addAttribute("nextMonthFirst", nextMonthFirst);
        model.addAttribute("nextDisabled", nextDisabled);
        model.addAttribute("today", today);

        model.addAttribute("habits", habits);
        model.addAttribute("habitMonthStatus", habitMonthStatus);
        model.addAttribute("newPage", "month");

        return "index";
    }



//    @GetMapping("/habits/{id}")
//    public String showHabitDetails(@PathVariable Long id, Model model) {
//        Habit habit = habitService.findById(id);
//        model.addAttribute("habit", habit);
//        model.addAttribute("newPage", "detailsHabit");
//        return "index";
//    }

    @GetMapping("/habits/{id}")
    public String showHabitDetails(@PathVariable Long id,
                                   @RequestParam(required = false) Integer year,
                                   @RequestParam(required = false) Integer month,
                                   Model model) {
        Habit habit = habitService.findById(id);
        LocalDate today = LocalDate.now();

        // ----- Month context (allows prev/next month, capped at current month) -----
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;
        if (m < 1 || m > 12) m = today.getMonthValue();

        LocalDate first = LocalDate.of(y, m, 1);
        LocalDate thisMonthFirst = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        if (first.isAfter(thisMonthFirst)) {
            return "redirect:/habits/" + id + "?year=" + today.getYear() + "&month=" + today.getMonthValue();
        }

        LocalDate prevMonthFirst = first.minusMonths(1).withDayOfMonth(1);
        LocalDate nextCandidate  = first.plusMonths(1).withDayOfMonth(1);
        LocalDate nextMonthFirst = nextCandidate.isAfter(thisMonthFirst) ? thisMonthFirst : nextCandidate;
        boolean nextDisabled = first.equals(thisMonthFirst);

        // Days of chosen month
        List<LocalDate> monthDays = new ArrayList<>();
        for (int d = 1; d <= first.lengthOfMonth(); d++) {
            monthDays.add(first.withDayOfMonth(d));
        }

        // Status map for chosen month
        Map<LocalDate, Boolean> monthStatus = new HashMap<>();
        for (LocalDate d : monthDays) {
            monthStatus.put(d, habitService.isHabitDoneAtDate(habit.getId(), d));
        }

        // Month stats (count only up to today in the current month)
        int denom;
        int doneCountMonth = 0;
        if (first.getYear() == today.getYear() && first.getMonthValue() == today.getMonthValue()) {
            denom = today.getDayOfMonth();
            for (int d = 1; d <= denom; d++) {
                if (Boolean.TRUE.equals(monthStatus.get(first.withDayOfMonth(d)))) doneCountMonth++;
            }
        } else {
            denom = first.lengthOfMonth();
            for (LocalDate d : monthDays) {
                if (Boolean.TRUE.equals(monthStatus.get(d))) doneCountMonth++;
            }
        }
        double completionRateMonth = denom == 0 ? 0.0 : (doneCountMonth * 100.0 / denom);

        // Streaks (up to today)
        int currentStreak = 0;
        LocalDate cursor = today;
        while (!cursor.isBefore(habit.getStartDate() != null ? habit.getStartDate() : LocalDate.of(1970,1,1))
                && habitService.isHabitDoneAtDate(habit.getId(), cursor)) {
            currentStreak++;
            cursor = cursor.minusDays(1);
        }

        // Longest streak in current year
        int yearOfStats = today.getYear();
        int longestStreak = 0, running = 0;
        LocalDate day = LocalDate.of(yearOfStats, 1, 1);
        while (!day.isAfter(today)) {
            if (habitService.isHabitDoneAtDate(habit.getId(), day)) {
                running++;
                longestStreak = Math.max(longestStreak, running);
            } else {
                running = 0;
            }
            day = day.plusDays(1);
        }

        // Year overview: per-month done counts vs totals
        List<Integer> monthlyDoneCounts = new ArrayList<>(12);
        List<Integer> monthlyTotals = new ArrayList<>(12);

        for (int mm = 1; mm <= 12; mm++) {
            LocalDate firstOfM = LocalDate.of(yearOfStats, mm, 1);
            int len = firstOfM.lengthOfMonth();

            // robust future detection (also covers if you ever change yearOfStats)
            boolean isFutureMonth =
                    (yearOfStats > today.getYear()) ||
                            (yearOfStats == today.getYear() && mm > today.getMonthValue());

            int totalForMonth;
            int done = 0;

            if (isFutureMonth) {
                // future month → nothing has happened yet
                totalForMonth = 0;
                // done stays 0
            } else if (yearOfStats == today.getYear() && mm == today.getMonthValue()) {
                // current month → count only up to today
                totalForMonth = today.getDayOfMonth();
                for (int d = 1; d <= totalForMonth; d++) {
                    LocalDate dt = firstOfM.withDayOfMonth(d);
                    if (habitService.isHabitDoneAtDate(habit.getId(), dt)) done++;
                }
            } else {
                // past month → full month
                totalForMonth = len;
                for (int d = 1; d <= len; d++) {
                    LocalDate dt = firstOfM.withDayOfMonth(d);
                    if (habitService.isHabitDoneAtDate(habit.getId(), dt)) done++;
                }
            }

            monthlyDoneCounts.add(done);
            monthlyTotals.add(totalForMonth);
        }

        // Labels
        String monthHeadline = first.getMonth().name().substring(0,1)
                + first.getMonth().name().substring(1).toLowerCase() + " " + y;

        //for the link below the year month stats
        List<Boolean> monthIsFuture = new ArrayList<>(12);
        for (int mm = 1; mm <= 12; mm++) {
            LocalDate firstOfM = LocalDate.of(yearOfStats, mm, 1);
            int len = firstOfM.lengthOfMonth();

            boolean isFutureMonth =
                    (yearOfStats > today.getYear()) ||
                            (yearOfStats == today.getYear() && mm > today.getMonthValue());

            int totalForMonth;
            int done = 0;

            if (isFutureMonth) {
                totalForMonth = 0; // show 0/0
            } else if (yearOfStats == today.getYear() && mm == today.getMonthValue()) {
                totalForMonth = today.getDayOfMonth();
                for (int d = 1; d <= totalForMonth; d++) {
                    if (habitService.isHabitDoneAtDate(habit.getId(), firstOfM.withDayOfMonth(d))) done++;
                }
            } else {
                totalForMonth = len;
                for (int d = 1; d <= len; d++) {
                    if (habitService.isHabitDoneAtDate(habit.getId(), firstOfM.withDayOfMonth(d))) done++;
                }
            }

            monthlyDoneCounts.add(done);
            monthlyTotals.add(totalForMonth);
            monthIsFuture.add(isFutureMonth);
        }

        // Model
        model.addAttribute("habit", habit);
        model.addAttribute("monthHeadline", monthHeadline);
        model.addAttribute("monthDays", monthDays);
        model.addAttribute("monthStatus", monthStatus);
        model.addAttribute("prevMonthFirst", prevMonthFirst);
        model.addAttribute("nextMonthFirst", nextMonthFirst);
        model.addAttribute("nextDisabled", nextDisabled);
        model.addAttribute("today", today);

        model.addAttribute("doneCountMonth", doneCountMonth);
        model.addAttribute("completionRateMonth", completionRateMonth);
        model.addAttribute("currentStreak", currentStreak);
        model.addAttribute("longestStreak", longestStreak);
        model.addAttribute("monthlyDoneCounts", monthlyDoneCounts);
        model.addAttribute("monthlyTotals", monthlyTotals);
        model.addAttribute("yearOfStats", yearOfStats);
        model.addAttribute("monthIsFuture", monthIsFuture);

        model.addAttribute("newPage", "detailsHabit");
        return "index";
    }





    @PostMapping("/habits/delete/{id}")
    public String deleteGoal(@PathVariable Long id) {
        habitService.deleteById(id);
        return "redirect:/habits";
    }


}
