package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Category;
import com.ozzo.habit_tracker.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryViewController {

    private final CategoryService categoryService;

    public CategoryViewController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String showCategories(Model model) {
        List<Category> allCategories = categoryService.findAll();
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("newPage", "categories");
        return "index";
    }


}
