package com.example.habittracker.exception;

public class HabitAccessDeniedException extends RuntimeException {

    public HabitAccessDeniedException(Long habitId) {
        super("You do not have permission to access habit with id " + habitId);
    }
}