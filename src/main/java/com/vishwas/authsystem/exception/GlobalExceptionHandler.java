package com.vishwas.authsystem.exception;

// Create global exception handler.
// Requirements:
// - Annotate with @RestControllerAdvice
// - Handle UserAlreadyExistsException
// - Return ResponseEntity with BAD_REQUEST status
// - Return error message in response body


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
