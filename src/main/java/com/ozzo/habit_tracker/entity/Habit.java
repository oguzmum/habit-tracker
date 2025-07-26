package com.ozzo.habit_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private Integer priority;
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Enumerated(EnumType.STRING) //this way the enums are safed as readable strings in the DB
    @Column(name = "frequency_type")
    private FrequencyType frequencyType;

    @Column(name = "frequency_value")
    private Integer frequencyValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitEntry> habitEntries = new ArrayList<>();

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitStreak> habitStreaks = new ArrayList<>();


    public Habit() {}

    public Habit(String name, String description) {
        this.name = name;
        this.description = description;
        this.startDate = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public Goal getGoal() { return goal; }
    public void setGoal(Goal goal) { this.goal = goal; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<HabitEntry> getHabitEntries() { return habitEntries; }
    public void setHabitEntries(List<HabitEntry> habitEntries) { this.habitEntries = habitEntries; }

    public List<HabitStreak> getHabitStreaks() { return habitStreaks; }
    public void setHabitStreaks(List<HabitStreak> habitStreaks) { this.habitStreaks = habitStreaks; }

    public FrequencyType getFrequencyType() { return frequencyType; }
    public void setFrequencyType(FrequencyType frequencyType) { this.frequencyType = frequencyType; }

    public Integer getFrequencyValue() { return frequencyValue; }
    public void setFrequencyValue(Integer frequencyValue) { this.frequencyValue = frequencyValue; }
}