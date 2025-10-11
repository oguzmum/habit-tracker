package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByNameIgnoreCase(String name);
}
