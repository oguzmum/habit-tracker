package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.repository.AppUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final AppUserRepository users;

    public AuthController(AppUserRepository users) {
        this.users = users;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("needsAdmin", users.count() == 0);
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
