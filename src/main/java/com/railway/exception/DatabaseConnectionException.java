package com.railway.exception;

/**
 * Exception thrown when there are issues with database connectivity.
 */
public class DatabaseConnectionException extends Exception {
    
    public DatabaseConnectionException(String message) {
        super(message);
    }
    
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}