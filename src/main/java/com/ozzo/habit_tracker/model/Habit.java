package com.ozzo.habit_tracker.model;

import java.io.Serializable;
import java.sql.Date;

public class Habit implements Serializable {
    private Integer id;
    private String name;
    private Integer priority;
    private String description;
    private Date start_date;
    private boolean completed;
    //foreig key with  goal_id and category-id


    public Habit(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Habit(Integer id, String name, boolean completed) {
        this.completed = completed;
        this.id = id;
        this.name = name;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}


