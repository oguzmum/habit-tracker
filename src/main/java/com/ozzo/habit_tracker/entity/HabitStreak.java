package com.ozzo.habit_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "habit_streaks")
public class HabitStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    public HabitStreak() {}

    public HabitStreak(Habit habit, LocalDate startDate, LocalDate endDate) {
        this.habit = habit;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    //not as column in table as it wouldn't make any sense :D
    public int getStreakLength() {
        if (startDate != null && endDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        } else {
            return 0;
        }
    }

}
