package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.model.Habit;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HabitService {

    private final List<Habit> dummyHabits = new ArrayList<>();

    // after Bean Start, fill the list dummyHabits
    @PostConstruct
    void init() {
        dummyHabits.add(new Habit(1, "10 000 Schritte", true));
        dummyHabits.add(new Habit(2, "Trainieren",       true));
        dummyHabits.add(new Habit(3, "Lesen",            false));
    }

    public List<Habit> findAll() {
        return Collections.unmodifiableList(dummyHabits);   // read-only View
    }

    public Habit findById(long id) {
        for(Habit habit : dummyHabits){
            if(habit.getId() == id){
                return habit;
            }
        }
        return null;
    }

    public void add(Habit habit) {
        dummyHabits.add(habit);
    }
}
