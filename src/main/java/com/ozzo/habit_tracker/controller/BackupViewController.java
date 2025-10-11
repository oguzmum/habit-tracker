package com.ozzo.habit_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BackupViewController {

    @GetMapping("/backup")
    public String showBackupPage(Model model) {
        model.addAttribute("newPage", "backup");
        return "index";
    }
}
