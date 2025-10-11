package com.ozzo.habit_tracker.backup;

import com.ozzo.habit_tracker.entity.FrequencyType;

import java.time.LocalDate;

public record HabitData(
        Long id,
        String name,
        Integer priority,
        String description,
        LocalDate startDate,
        FrequencyType frequencyType,
        Integer frequencyValue,
        Long goalId,
        Long categoryId
) {
}
