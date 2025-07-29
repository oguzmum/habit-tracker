package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.Goal;
import com.ozzo.habit_tracker.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    private List<Goal> allGoals = new ArrayList<>();

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<Goal> findAll(){
        allGoals = goalRepository.findAll();
        return allGoals;
    }
}
