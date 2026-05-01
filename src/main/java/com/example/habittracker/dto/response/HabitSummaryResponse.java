package com.example.habittracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HabitSummaryResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private boolean completedToday;

    private int currentStreak;

    private int longestStreak;

    private long completedDaysLast7Days;

    private double completionRateLast7Days;
}