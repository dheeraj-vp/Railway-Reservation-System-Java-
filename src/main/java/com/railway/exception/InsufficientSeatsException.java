package com.railway.exception;

/**
 * Exception thrown when the requested number of seats exceeds available seats.
 */
public class InsufficientSeatsException extends Exception {
    
    public InsufficientSeatsException(String message) {
        super(message);
    }
    
    public InsufficientSeatsException(String message, Throwable cause) {
        super(message, cause);
    }
}