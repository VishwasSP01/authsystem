package com.vishwas.authsystem.exception;

// Create custom exception for duplicate users.

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
