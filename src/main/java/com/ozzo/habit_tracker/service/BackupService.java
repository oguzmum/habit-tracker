package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.backup.BackupData;
import com.ozzo.habit_tracker.backup.CategoryData;
import com.ozzo.habit_tracker.backup.GoalData;
import com.ozzo.habit_tracker.backup.HabitData;
import com.ozzo.habit_tracker.backup.HabitEntryData;
import com.ozzo.habit_tracker.repository.CategoryRepository;
import com.ozzo.habit_tracker.repository.GoalRepository;
import com.ozzo.habit_tracker.repository.HabitEntryRepository;
import com.ozzo.habit_tracker.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BackupService {

    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;
    private final HabitRepository habitRepository;
    private final HabitEntryRepository habitEntryRepository;

    public BackupService(CategoryRepository categoryRepository,
                         GoalRepository goalRepository,
                         HabitRepository habitRepository,
                         HabitEntryRepository habitEntryRepository) {
        this.categoryRepository = categoryRepository;
        this.goalRepository = goalRepository;
        this.habitRepository = habitRepository;
        this.habitEntryRepository = habitEntryRepository;
    }

    @Transactional(readOnly = true)
    public BackupData createBackup() {
        List<CategoryData> categories = categoryRepository.findAll().stream()
                .map(category -> new CategoryData(category.getId(), category.getName(), category.getDescription()))
                .toList();

        List<GoalData> goals = goalRepository.findAll().stream()
                .map(goal -> new GoalData(goal.getId(), goal.getName(), goal.getDescription()))
                .toList();

        List<HabitData> habits = habitRepository.findAll().stream()
                .map(habit -> new HabitData(
                        habit.getId(),
                        habit.getName(),
                        habit.getPriority(),
                        habit.getDescription(),
                        habit.getStartDate(),
                        habit.getFrequencyType(),
                        habit.getFrequencyValue(),
                        habit.getGoal() != null ? habit.getGoal().getId() : null,
                        habit.getCategory() != null ? habit.getCategory().getId() : null
                ))
                .toList();

        List<HabitEntryData> entries = habitEntryRepository.findAll().stream()
                .map(entry -> new HabitEntryData(entry.getId(), entry.getHabit().getId(), entry.getDate()))
                .toList();

        return new BackupData(LocalDateTime.now(), categories, goals, habits, entries);
    }
}
