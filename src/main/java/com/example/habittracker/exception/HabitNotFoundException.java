package com.example.habittracker.exception;

public class HabitNotFoundException extends RuntimeException {

    public HabitNotFoundException(Long habitId) {
        super("Habit with id " + habitId + " not found");
    }
}