package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.entity.HabitEntry;
import com.ozzo.habit_tracker.repository.HabitRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HabitService {

    private final List<Habit> dummyHabits = new ArrayList<>();

    private final List<Habit> allHabits = new ArrayList<>();

    private final List<HabitEntry> dummyEntries = new ArrayList<>();

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }


    // this is executed after Bean is initalized/called
    @PostConstruct
    void init() {
//        dummyHabits.add(new Habit(1, "10 000 Schritte", true));
//        dummyHabits.add(new Habit(2, "Trainieren", true));
//        dummyHabits.add(new Habit(3, "Lesen", false));
//
//        dummyEntries.add(new HabitEntry(dummyEntries.size()+1, LocalDate.now(), findById(1)));
//        dummyEntries.add(new HabitEntry(dummyEntries.size()+1, LocalDate.now().plusDays(2), findById(1)));
//        dummyEntries.add(new HabitEntry(dummyEntries.size()+1, LocalDate.now(), findById(2)));
//        dummyEntries.add(new HabitEntry(dummyEntries.size()+1, LocalDate.now().plusDays(2), findById(2)));
    }

    public List<Habit> findAll() {
//        return dummyHabits;
        return habitRepository.findAll();
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

    public void markHabitAsDone(Integer id, LocalDate date){
        Habit habit = findById(id);
        if (habit != null) {
//            dummyEntries.add(new HabitEntry(dummyEntries.size() + 1, date, habit));
            System.out.println("mark habit as done here - persist in DB");
        }
    }

    public void markHabitAsUndone(Integer id, LocalDate date){
        // remove all entries for this habit on that date
        dummyEntries.removeIf(entry ->
                entry.getHabit().getId().equals(id)
                        && entry.getDate().equals(date)
        );
    }

    public boolean isHabitDoneToday(Long habitId) {
        LocalDate today = LocalDate.now();
        return dummyEntries.stream()
                .anyMatch(entry -> entry.getHabit().getId().equals(habitId)
                        && entry.getDate().equals(today));
    }

    public boolean isHabitDoneAtDate(Long habitId, LocalDate date) {
        return dummyEntries.stream()
                .anyMatch(entry -> entry.getHabit().getId().equals(habitId)
                        && entry.getDate().equals(date));
    }
}
