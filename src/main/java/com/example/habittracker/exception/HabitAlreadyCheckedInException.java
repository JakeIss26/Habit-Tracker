package com.example.habittracker.exception;


public class HabitAlreadyCheckedInException extends RuntimeException {

    public HabitAlreadyCheckedInException(Long habitId) {
        super("Habit with id " + habitId + " is already checked in today");
    }
}