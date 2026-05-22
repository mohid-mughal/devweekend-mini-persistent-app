package com.knowledgebase;

/**
 * Exception thrown when note data validation fails.
 * Provides descriptive error messages for validation failures.
 */
public class ValidationException extends Exception {
    
    /**
     * Creates a new ValidationException with the specified error message.
     * 
     * @param message the descriptive error message
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Creates a new ValidationException with the specified error message and cause.
     * 
     * @param message the descriptive error message
     * @param cause the underlying cause of the validation failure
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
