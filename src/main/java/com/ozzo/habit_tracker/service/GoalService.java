package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.Goal;
import com.ozzo.habit_tracker.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<Goal> findAll(){
        return goalRepository.findAll();
    }

    public Goal save(Goal goal){
        return goalRepository.save(goal);
    }
}
