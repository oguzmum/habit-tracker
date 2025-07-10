package com.ozzo.habit_tracker;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String settingsPage() {
        return "test"; // → templates/settings.html
    }
}