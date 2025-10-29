package com.railway.exception;

/**
 * Exception thrown when a train with the specified ID is not found.
 */
public class TrainNotFoundException extends Exception {
    
    public TrainNotFoundException(String message) {
        super(message);
    }
    
    public TrainNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}