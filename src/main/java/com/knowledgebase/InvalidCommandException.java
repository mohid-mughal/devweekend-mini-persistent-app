package com.knowledgebase;

/**
 * Exception thrown when a command cannot be parsed or is invalid.
 * 
 * Requirements: 8.1, 8.3
 */
public class InvalidCommandException extends Exception {
    
    /**
     * Creates a new InvalidCommandException with the specified message.
     * 
     * @param message the error message
     */
    public InvalidCommandException(String message) {
        super(message);
    }
    
    /**
     * Creates a new InvalidCommandException with the specified message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
