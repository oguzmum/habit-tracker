package com.ozzo.habit_tracker.backup;

import java.time.LocalDate;

public record HabitEntryData(Long id, Long habitId, LocalDate date) {
}
