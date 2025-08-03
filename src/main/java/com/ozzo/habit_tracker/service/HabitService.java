package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.entity.HabitEntry;
import com.ozzo.habit_tracker.repository.HabitEntryRepository;
import com.ozzo.habit_tracker.repository.HabitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitService {

    private static final Logger log = LoggerFactory.getLogger(HabitService.class);

    private final HabitRepository habitRepository;
    private final HabitEntryRepository habitEntryRepository;

    public HabitService(HabitRepository habitRepository, HabitEntryRepository habitEntryRepository) {
        this.habitRepository = habitRepository;
        this.habitEntryRepository = habitEntryRepository;
    }


    public List<Habit> findAll() {
        return habitRepository.findAll();
    }

    public Habit findById(long id) {
        List<Habit> allHabits = this.findAll();
        for(Habit habit : allHabits){
            if(habit.getId() == id){
                return habit;
            }
        }
        return null;
    }

    public void add(Habit habit) {
        //insert into Db is done via the save function
        // also comes default with the JpaRepository :D
        habitRepository.save(habit);
    }

    public void markHabitAsDone(Long id, LocalDate date){
        Habit habit = findById(id);
        if (habit == null) {
            log.warn("Habit with ID {} not found", id);
            return;
        }

        boolean exists = habitEntryRepository.findByHabitIdAndDate(habit.getId(), date).isPresent();
        if (!exists) {
            HabitEntry entry = new HabitEntry();
            entry.setHabit(habit);
            entry.setDate(date);
            habitEntryRepository.save(entry);
            log.info("Marked habit {} as done for {}", habit.getName(), date);
        } else {
            log.info("Habit {} already marked as done for {}", habit.getName(), date);
        }
    }

    public void markHabitAsUndone(Long id, LocalDate date){
        habitEntryRepository.deleteByHabitIdAndDate(id, date);

        //alternatively if I dont want to do it via interface function
//        habitEntryRepository.findByHabitIdAndDate(id, date)
//                .ifPresent(entry -> habitEntryRepository.delete(entry));

        log.info("Marked habit {} as UNdone for {}", id, date);
    }

    public boolean isHabitDoneToday(Long habitId) {
        LocalDate today = LocalDate.now();
        return habitEntryRepository.findByHabitIdAndDate(habitId, today).isPresent();
    }

    public boolean isHabitDoneAtDate(Long habitId, LocalDate date) {
        return habitEntryRepository.findByHabitIdAndDate(habitId, date).isPresent();
    }

    public Habit save(Habit habit){
        return habitRepository.save(habit);
    }
}
