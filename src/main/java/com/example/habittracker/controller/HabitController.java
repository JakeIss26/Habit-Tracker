package com.example.habittracker.controller;

import com.example.habittracker.dto.request.HabitCreateRequest;
import com.example.habittracker.dto.request.HabitUpdateRequest;
import com.example.habittracker.dto.response.HabitResponse;
import com.example.habittracker.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitResponse createHabit(@RequestBody HabitCreateRequest request) {
        return habitService.createHabit(request);
    }

    @GetMapping
    public List<HabitResponse> getActiveHabits() {
        return habitService.getActiveHabits();
    }

    @GetMapping("/{id}")
    public HabitResponse getHabitById(@PathVariable Long id) {
        return habitService.getHabitById(id);
    }

    @PatchMapping("/{id}")
    public HabitResponse updateHabit(@PathVariable Long id, @RequestBody HabitUpdateRequest request) {
        return habitService.updateHabit(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveHabit(@PathVariable Long id) {
        habitService.archiveHabit(id);
    }
}