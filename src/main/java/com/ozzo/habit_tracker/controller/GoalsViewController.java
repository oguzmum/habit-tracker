package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Goal;
import com.ozzo.habit_tracker.service.GoalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GoalsViewController {

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

}
