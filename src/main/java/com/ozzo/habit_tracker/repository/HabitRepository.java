package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    //to find all Habits there is no function necessary :D
}
