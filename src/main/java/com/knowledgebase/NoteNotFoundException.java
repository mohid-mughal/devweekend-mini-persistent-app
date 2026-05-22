package com.knowledgebase;

/**
 * Exception thrown when a requested note cannot be found.
 * Used by NoteManager when operations reference non-existent notes.
 */
public class NoteNotFoundException extends Exception {
    
    /**
     * Creates a new NoteNotFoundException with the specified error message.
     * 
     * @param message the descriptive error message
     */
    public NoteNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Creates a new NoteNotFoundException with the specified error message and cause.
     * 
     * @param message the descriptive error message
     * @param cause the underlying cause of the exception
     */
    public NoteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
