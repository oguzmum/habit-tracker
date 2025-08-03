package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.Category;
import com.ozzo.habit_tracker.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class CategoryViewController {

    private static final Logger log = LoggerFactory.getLogger(CategoryViewController.class);

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

    @GetMapping("/categories/new-page")
    public String showNewHabitForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("newPage", "addNewCategory");
        return "index";
    }

    @PostMapping("/categories/form")
    public String saveGoal(@ModelAttribute("category") Category category) {
        categoryService.save(category);
        log.info("Saved new Category {} with id {}", category.getName(), category.getId());
        return "redirect:/categories";
    }

    @GetMapping("/categories/{id}")
    public String showCategoryDetails(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("newPage", "detailsCategory");
        return "index";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return "redirect:/categories";
    }


}
