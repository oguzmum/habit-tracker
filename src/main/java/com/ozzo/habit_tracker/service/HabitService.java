package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.Habit;
import com.ozzo.habit_tracker.entity.HabitEntry;
import com.ozzo.habit_tracker.repository.HabitEntryRepository;
import com.ozzo.habit_tracker.repository.HabitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
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
        return habitRepository.findAll(defaultSort());
    }

    public Habit findById(long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found with id: " + id));
    }

    public void add(Habit habit) {
        //insert into Db is done via the save function
        // also comes default with the JpaRepository :D
        save(habit);
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
        if (habit.getSortOrder() == null) {
            habit.setSortOrder(nextSortOrderNumber());
        }
        return habitRepository.save(habit);
    }

    public void moveHabit(Long habitId, String direction) {
        int delta = switch (direction) {
            case "up" -> -1;
            case "down" -> 1;
            default -> 0;
        };

        if (delta == 0) {
            return;
        }

        List<Habit> orderedHabits = findAll();
        int index = -1;
        for (int i = 0; i < orderedHabits.size(); i++) {
            if (orderedHabits.get(i).getId().equals(habitId)) {
                index = i;
                break;
            }
        }

        int swapIndex = index + delta;
        if (index < 0 || swapIndex < 0 || swapIndex >= orderedHabits.size()) {
            return;
        }

        Habit current = orderedHabits.get(index);
        Habit target = orderedHabits.get(swapIndex);

        Integer currentOrder = current.getSortOrder();
        current.setSortOrder(target.getSortOrder());
        target.setSortOrder(currentOrder);

        habitRepository.save(current);
        habitRepository.save(target);
    }

    private Sort defaultSort() {
        return Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("id"));
    }

    private int nextSortOrderNumber() {
        Integer max = habitRepository.findMaxSortOrder();
        return (max == null ? 0 : max) + 1;
    }

    public void deleteById(Long id){
        habitRepository.deleteById(id);
    }
}
