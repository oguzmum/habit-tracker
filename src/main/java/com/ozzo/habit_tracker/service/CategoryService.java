package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.Category;
import com.ozzo.habit_tracker.repository.CategoryRepository;
import org.hibernate.sql.ast.tree.cte.CteColumn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Category save(Category category){
        return categoryRepository.save(category);
    }

    public Category findById(Long id) {
        //alternative is to use Optional<Category> as return Type, but i want a clear exception if a entry is not found
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public void deleteById(Long id){
        categoryRepository.deleteById(id);
    }

}
