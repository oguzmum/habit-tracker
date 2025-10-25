package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT g.year FROM Goal g WHERE g.year IS NOT NULL ORDER BY g.year DESC")
    List<Integer> findDistinctYears();
}
