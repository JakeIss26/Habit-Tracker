package com.example.habittracker.exception;

public class CheckInNotFoundException extends RuntimeException {

    public CheckInNotFoundException(Long checkInId) {
        super("Check-in with id " + checkInId + " not found");
    }
}