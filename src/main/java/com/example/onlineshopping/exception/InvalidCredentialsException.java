package com.example.onlineshopping.exception;

/**
 * Custom exception for handling authentication failures in the application.
 * This exception is thrown when:
 * 1. A user attempts to log in with incorrect credentials
 * 2. Username/password combination doesn't match stored values
 * 3. The user account doesn't exist
 */
public class InvalidCredentialsException extends RuntimeException {

    // Default error message if none is provided
    private static final String DEFAULT_MESSAGE = "Invalid credentials provided";

    /**
     * Default constructor with standard error message
     */
    public InvalidCredentialsException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructor with custom error message
     * @param message The specific error message to use
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }

}
