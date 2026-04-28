package com.example.habittracker.controller;

import com.example.habittracker.dto.response.HabitCheckInResponse;
import com.example.habittracker.service.HabitCheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits/{habitId}/check-ins")
@RequiredArgsConstructor
public class HabitCheckInController {

    private final HabitCheckInService habitCheckInService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitCheckInResponse createCheckIn(@PathVariable Long habitId) {
        return habitCheckInService.createCheckIn(habitId);
    }

    @GetMapping
    public List<HabitCheckInResponse> getCheckIns(@PathVariable Long habitId) {
        return habitCheckInService.getCheckInsByHabitId(habitId);
    }
}