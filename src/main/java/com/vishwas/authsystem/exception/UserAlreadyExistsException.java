package com.vishwas.authsystem.exception;

// Create custom exception for duplicate users.
// Requirements:
// - Extend RuntimeException
// - Create constructor accepting message



public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
