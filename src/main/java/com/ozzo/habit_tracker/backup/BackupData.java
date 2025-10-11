package com.ozzo.habit_tracker.backup;

import java.time.LocalDateTime;
import java.util.List;

public record BackupData(
        LocalDateTime generatedAt,
        List<CategoryData> categories,
        List<GoalData> goals,
        List<HabitData> habits,
        List<HabitEntryData> habitEntries
) {
}
