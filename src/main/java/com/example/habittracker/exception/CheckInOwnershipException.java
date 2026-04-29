package com.example.habittracker.exception;

public class CheckInOwnershipException extends RuntimeException {

    public CheckInOwnershipException(Long checkInId, Long habitId) {
        super("Check-in with id " + checkInId + " does not belong to habit with id " + habitId);
    }
}