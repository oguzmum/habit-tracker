package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
