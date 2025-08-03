package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Goal;
import com.ozzo.habit_tracker.service.GoalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class GoalsViewController {

    private static final Logger log = LoggerFactory.getLogger(GoalsViewController.class);

    private final GoalService goalService;

    public GoalsViewController(GoalService goalService) {
        this.goalService = goalService;
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

}
