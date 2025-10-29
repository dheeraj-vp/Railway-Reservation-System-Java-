package com.railway.exception;

/**
 * Exception thrown when passenger's wallet balance is insufficient.
 */
public class InsufficientBalanceException extends Exception {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}