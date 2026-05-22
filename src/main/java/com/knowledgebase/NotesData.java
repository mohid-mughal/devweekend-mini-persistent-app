package com.knowledgebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for notes.json file.
 * Contains the next available ID and the list of all notes.
 * 
 * Requirements: 7.1, 7.5
 */
public class NotesData {
    
    private int nextId;
    private List<Note> notes;
    
    /**
     * Creates a new NotesData with default values.
     */
    public NotesData() {
        this.nextId = 1;
        this.notes = new ArrayList<>();
    }
    
    /**
     * Creates a new NotesData with specified values.
     * 
     * @param nextId the next available note ID
     * @param notes the list of notes
     */
    public NotesData(int nextId, List<Note> notes) {
        this.nextId = nextId;
        this.notes = notes != null ? new ArrayList<>(notes) : new ArrayList<>();
    }
    
    // Getters
    
    public int getNextId() {
        return nextId;
    }
    
    public List<Note> getNotes() {
        return notes;
    }
    
    // Setters
    
    public void setNextId(int nextId) {
        this.nextId = nextId;
    }
    
    public void setNotes(List<Note> notes) {
        this.notes = notes != null ? new ArrayList<>(notes) : new ArrayList<>();
    }
}
