package com.example.habittracker.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HabitUpdateRequest {

    private String title;

    private String description;
}
