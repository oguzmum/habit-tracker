package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.backup.BackupData;
import com.ozzo.habit_tracker.backup.BackupImportResult;
import com.ozzo.habit_tracker.backup.CategoryData;
import com.ozzo.habit_tracker.backup.GoalData;
import com.ozzo.habit_tracker.backup.HabitData;
import com.ozzo.habit_tracker.backup.HabitEntryData;
import com.ozzo.habit_tracker.entity.Category;
import com.ozzo.habit_tracker.entity.Goal;
import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.entity.HabitEntry;
import com.ozzo.habit_tracker.repository.CategoryRepository;
import com.ozzo.habit_tracker.repository.GoalRepository;
import com.ozzo.habit_tracker.repository.HabitEntryRepository;
import com.ozzo.habit_tracker.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Transactional
    public BackupImportResult importBackup(BackupData data) {
        if (data == null) {
            throw new IllegalArgumentException("Backup-Data is null.");
        }

        Map<Long, Category> categoryMap = new HashMap<>();
        int categoriesImported = 0;
        int categoriesSkipped = 0;
        if (data.categories() != null) {
            for (CategoryData categoryData : data.categories()) {
                if (categoryData == null || categoryData.name() == null || categoryData.name().isBlank()) {
                    categoriesSkipped++;
                    continue;
                }

                Long backupId = categoryData.id();
                if (backupId == null) {
                    categoriesSkipped++;
                    continue;
                }

                Optional<Category> existing = categoryRepository.findById(backupId)
                        .or(() -> categoryRepository.findByNameIgnoreCase(categoryData.name()));

                if (existing.isPresent()) {
                    categoryMap.put(backupId, existing.get());
                    categoriesSkipped++;
                    continue;
                }

                Category category = new Category();
                category.setName(categoryData.name());
                category.setDescription(categoryData.description());
                Category saved = categoryRepository.save(category);
                categoryMap.put(backupId, saved);
                categoriesImported++;
            }
        }

        Map<Long, Goal> goalMap = new HashMap<>();
        int goalsImported = 0;
        int goalsSkipped = 0;
        if (data.goals() != null) {
            for (GoalData goalData : data.goals()) {
                if (goalData == null || goalData.name() == null || goalData.name().isBlank()) {
                    goalsSkipped++;
                    continue;
                }

                Long backupId = goalData.id();
                if (backupId == null) {
                    goalsSkipped++;
                    continue;
                }

                Optional<Goal> existing = goalRepository.findById(backupId)
                        .or(() -> goalRepository.findByNameIgnoreCase(goalData.name()));

                if (existing.isPresent()) {
                    goalMap.put(backupId, existing.get());
                    goalsSkipped++;
                    continue;
                }

                Goal goal = new Goal();
                goal.setName(goalData.name());
                goal.setDescription(goalData.description());
                Goal saved = goalRepository.save(goal);
                goalMap.put(backupId, saved);
                goalsImported++;
            }
        }

        Map<Long, Habit> habitMap = new HashMap<>();
        int habitsImported = 0;
        int habitsSkipped = 0;
        if (data.habits() != null) {
            for (HabitData habitData : data.habits()) {
                if (habitData == null || habitData.name() == null || habitData.name().isBlank()) {
                    habitsSkipped++;
                    continue;
                }

                Long backupId = habitData.id();
                if (backupId == null) {
                    habitsSkipped++;
                    continue;
                }

                Optional<Habit> existing = habitRepository.findById(backupId)
                        .or(() -> habitRepository.findByNameIgnoreCase(habitData.name()));

                if (existing.isPresent()) {
                    habitMap.put(backupId, existing.get());
                    habitsSkipped++;
                    continue;
                }

                Habit habit = new Habit();
                habit.setName(habitData.name());
                habit.setPriority(habitData.priority());
                habit.setDescription(habitData.description());
                habit.setStartDate(habitData.startDate());
                habit.setFrequencyType(habitData.frequencyType());
                habit.setFrequencyValue(habitData.frequencyValue());

                if (habitData.goalId() != null) {
                    Goal goal = resolveGoal(goalMap, habitData.goalId());
                    habit.setGoal(goal);
                }

                if (habitData.categoryId() != null) {
                    Category category = resolveCategory(categoryMap, habitData.categoryId());
                    habit.setCategory(category);
                }

                Habit saved = habitRepository.save(habit);
                habitMap.put(backupId, saved);
                habitsImported++;
            }
        }

        int entriesImported = 0;
        int entriesSkipped = 0;
        if (data.habitEntries() != null) {
            for (HabitEntryData entryData : data.habitEntries()) {
                if (entryData == null || entryData.habitId() == null || entryData.date() == null) {
                    entriesSkipped++;
                    continue;
                }

                Habit habit = habitMap.get(entryData.habitId());
                if (habit == null) {
                    habit = resolveHabit(habitMap, entryData.habitId());
                    if (habit == null) {
                        entriesSkipped++;
                        continue;
                    }
                }

                boolean existsById = entryData.id() != null && habitEntryRepository.existsById(entryData.id());
                boolean existsByDate = habitEntryRepository.findByHabitIdAndDate(habit.getId(), entryData.date()).isPresent();

                if (existsById || existsByDate) {
                    entriesSkipped++;
                    continue;
                }

                HabitEntry entry = new HabitEntry();
                entry.setHabit(habit);
                entry.setDate(entryData.date());
                habitEntryRepository.save(entry);
                entriesImported++;
            }
        }

        return new BackupImportResult(
                categoriesImported,
                categoriesSkipped,
                goalsImported,
                goalsSkipped,
                habitsImported,
                habitsSkipped,
                entriesImported,
                entriesSkipped
        );
    }

    private Goal resolveGoal(Map<Long, Goal> goalMap, Long id) {
        if (id == null) {
            return null;
        }
        Goal goal = goalMap.get(id);
        if (goal != null) {
            return goal;
        }
        return goalRepository.findById(id)
                .map(found -> {
                    goalMap.put(id, found);
                    return found;
                })
                .orElse(null);
    }

    private Category resolveCategory(Map<Long, Category> categoryMap, Long id) {
        if (id == null) {
            return null;
        }
        Category category = categoryMap.get(id);
        if (category != null) {
            return category;
        }
        return categoryRepository.findById(id)
                .map(found -> {
                    categoryMap.put(id, found);
                    return found;
                })
                .orElse(null);
    }

    private Habit resolveHabit(Map<Long, Habit> habitMap, Long id) {
        if (id == null) {
            return null;
        }
        Habit habit = habitMap.get(id);
        if (habit != null) {
            return habit;
        }
        return habitRepository.findById(id)
                .map(found -> {
                    habitMap.put(id, found);
                    return found;
                })
                .orElse(null);
    }
}
