package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.model.Habit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
//this is liek the base route to be matched for all the below GetMapping routes
//e.g. for getAllHabits the request must be /habits/all
@RequestMapping("/habits")
public class HabitController {


}
