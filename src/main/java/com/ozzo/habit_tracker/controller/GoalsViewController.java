package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Goal;
import com.ozzo.habit_tracker.entity.Quarter;
import com.ozzo.habit_tracker.entity.SubGoal;
import com.ozzo.habit_tracker.service.GoalService;
import com.ozzo.habit_tracker.service.SubGoalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class GoalsViewController {

    private static final Logger log = LoggerFactory.getLogger(GoalsViewController.class);

    private static final Map<Quarter, List<Month>> QUARTER_MONTHS = Map.of(
            Quarter.Q1, List.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH),
            Quarter.Q2, List.of(Month.APRIL, Month.MAY, Month.JUNE),
            Quarter.Q3, List.of(Month.JULY, Month.AUGUST, Month.SEPTEMBER),
            Quarter.Q4, List.of(Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER)
    );

    private final GoalService goalService;
    private final SubGoalService subGoalService;

    public GoalsViewController(GoalService goalService, SubGoalService subGoalService) {
        this.goalService = goalService;
        this.subGoalService = subGoalService;
    }

    @GetMapping("/goals")
    public String showGoals(Model model) {
        List<Goal> allGoals = goalService.findAll();
        model.addAttribute("allGoals", allGoals);
        model.addAttribute("newPage", "goals");
        return "index";
    }

    @GetMapping("/goals/new-page")
    public String showNewHabitForm(Model model) {
        model.addAttribute("goal", new Goal());
        model.addAttribute("newPage", "addNewGoal");
        return "index";
    }

    @PostMapping("/goals/form")
    public String saveGoal(@ModelAttribute("goal") Goal goal) {
        goalService.save(goal);
        log.info("Saved new Goal {} with id {}", goal.getName(), goal.getId());
        return "redirect:/goals";
    }

    @GetMapping("/goals/{id}")
    public String showGoalDetails(@PathVariable Long id, Model model) {
        Goal goal = goalService.findById(id);
        model.addAttribute("goal", goal);
        model.addAttribute("newPage", "detailsGoal");
        return "index";
    }

    @PostMapping("/goals/delete/{id}")
    public String deleteGoal(@PathVariable Long id) {
        goalService.deleteById(id);
        return "redirect:/goals";
    }

    @GetMapping("/quarterly-planning")
    public String showQuarterPlanning(@RequestParam(value = "year", required = false) Integer requestedYear, Model model) {

        int selectedYear = subGoalService.resolveYear(requestedYear);

        Set<Integer> availableYears = new TreeSet<>((a, b) -> b.compareTo(a));
        availableYears.addAll(goalService.collectAvailableYears());
        availableYears.addAll(subGoalService.collectAvailableYears());
        availableYears.add(selectedYear);

        List<Goal> allGoals = goalService.findAll();
        List<Goal> topYearGoals = new ArrayList<>();
        for(Goal goal : allGoals){
            if(goal.isTopYearGoal() && goal.getYear().equals(requestedYear)){
                topYearGoals.add(goal);
            }
        }

        Map<Quarter, List<SubGoal>> byQuarter = subGoalService.groupByQuarter(selectedYear);

        Map<String, List<SubGoal>> quarterSubGoals = new HashMap<>();
        byQuarter.forEach((q, list) -> quarterSubGoals.put(q.name(), list));

        Map<Month, List<SubGoal>> monthSubGoals = subGoalService.groupByMonth(selectedYear);


        List<SubGoal> subGoalsThisYear = subGoalService.findByYear(selectedYear);
        Map<Long, List<SubGoal>> subGoalsByGoal = new HashMap<>();
        for (SubGoal sg : subGoalsThisYear) {
            if (sg.getGoal() != null && sg.getGoal().getId() != null) {
                subGoalsByGoal.computeIfAbsent(sg.getGoal().getId(), k -> new ArrayList<>()).add(sg);
            }
        }

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("availableYears", availableYears);
        model.addAttribute("topYearGoals", topYearGoals);
        model.addAttribute("subGoalsByGoal", subGoalsByGoal);
        model.addAttribute("quarters", Arrays.asList(Quarter.values()));
        model.addAttribute("quarterSubGoals", quarterSubGoals);
        model.addAttribute("monthSubGoals", monthSubGoals);
        model.addAttribute("monthGrid", buildMonthGrid());
        model.addAttribute("quarterLabels", buildQuarterLabels());
        model.addAttribute("monthLabels", buildMonthLabels());
        model.addAttribute("allGoals", allGoals);
        model.addAttribute("newPage", "quarterly-planning");
        return "index";
    }


    private List<List<Month>> buildMonthGrid() {
        List<List<Month>> rows = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Month> row = new ArrayList<>();
            for (Quarter quarter : Quarter.values()) {
                row.add(QUARTER_MONTHS.get(quarter).get(i));
            }
            rows.add(row);
        }
        return rows;
    }

    private Map<String, String> buildQuarterLabels() {
        return java.util.Map.of(
                Quarter.Q1.name(), "Quartal 1",
                Quarter.Q2.name(), "Quartal 2",
                Quarter.Q3.name(), "Quartal 3",
                Quarter.Q4.name(), "Quartal 4"
        );
    }

    private Map<String, String> buildMonthLabels() {
        Map<String, String> labels = new java.util.HashMap<>();
        for (Month month : Month.values()) {
            String displayName = month.getDisplayName(TextStyle.FULL, Locale.GERMAN);
            labels.put(month.name(),
                    displayName.substring(0, 1).toUpperCase(Locale.GERMAN) + displayName.substring(1));
        }
        return labels;
    }

    @PostMapping("/subgoals/create")
    public String create(
            @RequestParam String name,
            @RequestParam Long goalId,
            @RequestParam Integer year,
            @RequestParam(required = false) Quarter quarter,
            @RequestParam(required = false) Month month
    ) {
        Goal goal = goalService.findById(goalId);
        SubGoal sg = new SubGoal();
        sg.setName(name);
        sg.setGoal(goal);
        sg.setYear(year);
        sg.setQuarter(quarter);
        sg.setMonth(month);
        subGoalService.save(sg);
        return "redirect:/quarterly-planning?year=" + year;
    }

    @PostMapping("/subgoals/update/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Long goalId,
            @RequestParam Integer year,
            @RequestParam(required = false) Quarter quarter,
            @RequestParam(required = false) Month month
    ) {
        Optional<SubGoal> subgoal = subGoalService.findById(id);
        SubGoal sg = subgoal.get();

        sg.setName(name);
        sg.setGoal(goalService.findById(goalId));
        sg.setYear(year);
        sg.setQuarter(quarter);
        sg.setMonth(month);
        subGoalService.save(sg);
        return "redirect:/quarterly-planning?year=" + year;
    }

    @PostMapping("/subgoals/delete/{id}")
    public String delete(@PathVariable Long id, @RequestParam Integer year) {
        subGoalService.deleteById(id);
        return "redirect:/quarterly-planning?year=" + year;
    }

}
