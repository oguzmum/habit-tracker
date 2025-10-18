package com.ozzo.habit_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Month;

@Entity
@Table(name = "sub_goals")
public class SubGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @NotBlank
    @Column(nullable = false)
    private String name;

    /**
     * Optional: can be null
     * for quarterly planing I will set it
     * otherwise not
     */
    @Column(name = "year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "quarter")
    private Quarter quarter;

    // I will use the java.time.Month lib
    @Enumerated(EnumType.STRING)
    @Column(name = "month")
    private Month month;

    @Min(0)
    @Max(100)
    @Column(name = "progress")
    private Integer progress = 0;

    @Column(name = "done", nullable = false)
    private boolean done = false;

    public SubGoal() {}

    public SubGoal(Goal goal, String name) {
        this.goal = goal;
        this.name = name;
    }

    public Long getId() { return id; }
    public Goal getGoal() { return goal; }
    public void setGoal(Goal goal) { this.goal = goal; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Quarter getQuarter() { return quarter; }
    public void setQuarter(Quarter quarter) { this.quarter = quarter; }

    public Month getMonth() { return month; }
    public void setMonth(Month month) { this.month = month; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
