package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.HabitEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HabitEntryRepository extends JpaRepository<HabitEntry, Long> {

    // Spring convention/concept called "derived queries" (https://www.baeldung.com/spring-data-derived-queries)
    // if the function name follows a specific naming pattern (e.g. findBy...()),
    // Spring can automatically generate the corresponding SQL/JPQL query in the background

    // for the delete operation it didn't work, possibly because it needed the id of the habit entity
    // so i had to overwrite it :D

    //optional datatype is like - this can have a value, but it is not necessary/expected
    Optional<HabitEntry> findByHabitIdAndDate(Long habitId, LocalDate date);


    @Transactional
    @Modifying
    @Query("DELETE FROM HabitEntry e WHERE e.habit.id = :habitId AND e.date = :date")
    void deleteByHabitIdAndDate(@Param("habitId") Long habitId, @Param("date") LocalDate date);
}
