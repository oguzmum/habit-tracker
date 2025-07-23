package com.ozzo.habit_tracker.model;

import java.io.Serializable;
import java.time.LocalDate;

public class HabitEntry implements Serializable {

    private Integer id;
    private LocalDate date;
    private Habit habit;

    public HabitEntry(Integer id, LocalDate date, Habit habit) {
        this.id = id;
        this.date = date;
        this.habit = habit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }
}
