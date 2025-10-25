package com.ozzo.habit_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Habit> habits = new ArrayList<>();

    private Integer year;

    @Min(0)
    @Max(100)
    @Column(name = "progress")
    private Integer progress = 0;

    @Min(0)
    @Max(10)
    private Integer priority;

    @Column(name = "done", nullable = false)
    private boolean done = false;

    @Column(name= "top_year_goal", nullable = false)
    private boolean topYearGoal = false;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubGoal> subGoals = new ArrayList<>();

    public Goal() {}

    public Goal(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Habit> getHabits() { return habits; }
    public void setHabits(List<Habit> habits) { this.habits = habits; }

    public Integer getYear() { return year; }
    public void setYear(Integer year){ this.year = year; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public boolean isDone() { return done; }
    public void setDone() { this.done = done; }

    public boolean isTopYearGoal() { return topYearGoal; }
    public void setTopYearGoal() { this.topYearGoal = topYearGoal; }

    public List<SubGoal> getSubGoals() { return subGoals; }
    public void setSubGoals(List<SubGoal> subGoals) { this.subGoals = subGoals; }
    public void addSubGoal(SubGoal sg) {
        subGoals.add(sg);
        sg.setGoal(this);
    }
    public void removeSubGoal(SubGoal sg) {
        subGoals.remove(sg);
        sg.setGoal(null);
    }
}