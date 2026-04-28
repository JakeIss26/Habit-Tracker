package com.example.habittracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HabitCheckInResponse {

    private Long id;

    private Long habitId;

    private LocalDate checkInDate;

    private LocalDateTime createdAt;
}