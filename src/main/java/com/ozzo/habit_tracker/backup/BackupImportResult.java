package com.ozzo.habit_tracker.backup;

public record BackupImportResult(
        int categoriesImported,
        int categoriesSkipped,
        int goalsImported,
        int goalsSkipped,
        int habitsImported,
        int habitsSkipped,
        int habitEntriesImported,
        int habitEntriesSkipped
) {
    public int totalImported() {
        return categoriesImported + goalsImported + habitsImported + habitEntriesImported;
    }

    public int totalSkipped() {
        return categoriesSkipped + goalsSkipped + habitsSkipped + habitEntriesSkipped;
    }

    public boolean hasImportedAnything() {
        return totalImported() > 0;
    }
}
