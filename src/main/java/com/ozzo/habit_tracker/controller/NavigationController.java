package com.ozzo.habit_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    /*
    * INFO: i always have to return index and not the requested page itself
    * otherwise the page isnt loaded in the main area, but the html page itself opens
    * */


//    @GetMapping({"/", "/daily", "/home"}) its possible to define multiple :D
    @GetMapping("/")
    public String showDaily(Model model) {
        model.addAttribute("newPage", "daily");
        return "index";
    }

    @GetMapping("/goals")
    public String showGoals(Model model) {
        model.addAttribute("newPage", "goals");
        return "index";
    }
}
