package com.railway.exception;

/**
 * Exception thrown when booking validation fails.
 */
public class InvalidBookingException extends Exception {
    
    public InvalidBookingException(String message) {
        super(message);
    }
    
    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}