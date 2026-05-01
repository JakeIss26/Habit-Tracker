package com.example.habittracker.controller;

import com.example.habittracker.dto.request.HabitCreateRequest;
import com.example.habittracker.dto.request.HabitUpdateRequest;
import com.example.habittracker.dto.response.HabitResponse;
import com.example.habittracker.dto.response.HabitStatsResponse;
import com.example.habittracker.dto.response.HabitSummaryResponse;
import com.example.habittracker.service.HabitService;
import jakarta.validation.Valid;
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
    public HabitResponse createHabit(@Valid @RequestBody HabitCreateRequest request) {
        return habitService.createHabit(request);
    }

    @GetMapping
    public List<HabitResponse> getActiveHabits() {
        return habitService.getActiveHabits();
    }

    @PutMapping("/{id}")
    public HabitResponse updateHabit(@PathVariable Long id, @Valid @RequestBody HabitUpdateRequest request) {
        return habitService.updateHabit(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveHabit(@PathVariable Long id) {
        habitService.archiveHabit(id);
    }

    @GetMapping("/{id}/stats")
    public HabitStatsResponse getHabitStats(@PathVariable Long id) {
        return habitService.getHabitStats(id);
    }
    @GetMapping("/summary")
    public List<HabitSummaryResponse> getHabitSummaries() {
        return habitService.getHabitSummaries();
    }

    @GetMapping("/archived")
    public List<HabitResponse> getArchivedHabits() {
        return habitService.getArchivedHabits();
    }

    @PatchMapping("/{id}/restore")
    public HabitResponse restoreHabit(@PathVariable Long id) {
        return habitService.restoreHabit(id);
    }

    @GetMapping("/{id}")
    public HabitResponse getHabitById(@PathVariable Long id) {
        return habitService.getHabitById(id);
    }

}