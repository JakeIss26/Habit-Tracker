package com.example.habittracker.exception;

import com.example.habittracker.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HabitNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHabitNotFound(HabitNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CheckInNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCheckInNotFound(CheckInNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(HabitAlreadyCheckedInException.class)
    public ResponseEntity<ErrorResponse> handleHabitAlreadyCheckedIn(HabitAlreadyCheckedInException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CheckInOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleCheckInOwnership(CheckInOwnershipException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
        .getFieldErrors()
        .getFirst()
        .getDefaultMessage();

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }
    @ExceptionHandler(HabitAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleHabitAccessDenied(HabitAccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }


    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse response = new ErrorResponse(
        status.value(),
        message,
        LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}