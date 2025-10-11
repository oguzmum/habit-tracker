package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    //to find all Habits there is no function necessary :D

    // for the import functionality - searching by the name
    Optional<Habit> findByNameIgnoreCase(String name);
}
