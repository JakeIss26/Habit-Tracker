package com.example.habittracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HabitStatsResponse {

    private Long habitId;

    private long totalCheckIns;

    private int currentStreak;

    private int longestStreak;

    private long completedDaysLast7Days;

    private double completionRateLast7Days;
}