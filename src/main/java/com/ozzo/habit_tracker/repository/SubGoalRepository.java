package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.SubGoal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long> {

    @EntityGraph(attributePaths = "goal")
    List<SubGoal> findByYear(Integer year);

    @EntityGraph(attributePaths = "goal")
    @Query("SELECT sg FROM SubGoal sg")
    List<SubGoal> findAllWithGoal();

}
