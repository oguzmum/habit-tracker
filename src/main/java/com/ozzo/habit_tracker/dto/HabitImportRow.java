package com.ozzo.habit_tracker.dto;

import java.util.ArrayList;
import java.util.List;

public class HabitImportRow {

    private Long habitId;
    private String habitName;
    private List<Boolean> days = new ArrayList<>();

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }


    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }


    public List<Boolean> getDays() {
        return days;
    }

    public void setDays(List<Boolean> days) {
        this.days = days;
    }
}
