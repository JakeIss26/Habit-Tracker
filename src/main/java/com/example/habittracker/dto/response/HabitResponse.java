package com.example.habittracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HabitResponse {

    private Long id;

    private String title;

    private String description;

    private boolean archived;

    private LocalDateTime createdAt;
}