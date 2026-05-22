package com.knowledgebase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages CRUD operations for notes using ArrayList for in-memory storage.
 * Coordinates with StorageLayer for persistence to JSON files.
 * 
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3
 */
public class NoteManager {
    
    private ArrayList<Note> notes;
    private StorageLayer storage;
    private int nextId;
    
    /**
     * Creates a new NoteManager with the specified storage layer.
     * 
     * @param storage the storage layer for persistence
     */
    public NoteManager(StorageLayer storage) {
        this.storage = storage;
        this.notes = new ArrayList<>();
        this.nextId = 1;
    }
    
    /**
     * Creates a new note with the specified data.
     * Validates note data before creation using Note validation methods.
     * Checks for duplicate titles (case-insensitive) in ArrayList.
     * Adds note to ArrayList with generated id and timestamps.
     * Calls storage.saveNotes() to persist to JSON file.
     * 
     * @param title the note title
     * @param content the note content
     * @param tags the list of tags (can be null or empty)
     * @return the created Note object
     * @throws ValidationException if note data is invalid or title is duplicate
     * @throws IOException if saving to storage fails
     */
    public Note createNote(String title, String content, List<String> tags) 
            throws ValidationException, IOException {
        // Validate note data before creation
        Note.validateNote(title, content, tags);
        
        // Check for duplicate titles (case-insensitive)
        List<String> existingTitles = notes.stream()
                .map(Note::getTitle)
                .collect(Collectors.toList());
        Note.validateTitleUniqueness(title, existingTitles);
        
        // Create note with generated id and timestamps
        LocalDateTime now = LocalDateTime.now();
        Note note = new Note(nextId, title, content, tags, now, now);
        
        // Add note to ArrayList
        notes.add(note);
        nextId++;
        
        // Persist to JSON file
        saveToStorage();
        
        return note;
    }
    
    /**
     * Retrieves a note by title (case-insensitive search).
     * 
     * @param title the note title to search for
     * @return Optional containing the note if found, empty otherwise
     */
    public Optional<Note> getNote(String title) {
        if (title == null) {
            return Optional.empty();
        }
        
        String titleLower = title.toLowerCase();
        return notes.stream()
                .filter(note -> note.getTitle().toLowerCase().equals(titleLower))
                .findFirst();
    }
    
    /**
     * Lists all notes in the ArrayList.
     * 
     * @return list of all notes
     */
    public List<Note> listNotes() {
        return new ArrayList<>(notes);
    }
    
    /**
     * Lists notes filtered by tag (case-insensitive).
     * 
     * @param tagFilter the tag to filter by
     * @return list of notes containing the specified tag
     */
    public List<Note> listNotes(String tagFilter) {
        if (tagFilter == null || tagFilter.isEmpty()) {
            return listNotes();
        }
        
        String tagLower = tagFilter.toLowerCase();
        return notes.stream()
                .filter(note -> note.getTags().stream()
                        .anyMatch(tag -> tag.toLowerCase().equals(tagLower)))
                .collect(Collectors.toList());
    }
    
    /**
     * Updates an existing note with new content and tags.
     * Validates updated data before modification.
     * Finds note in ArrayList by title (case-insensitive).
     * Updates note content, tags, and modifiedAt timestamp.
     * Calls storage.saveNotes() to persist changes.
     * 
     * @param title the title of the note to update
     * @param content the new content
     * @param tags the new list of tags
     * @return the updated Note object
     * @throws ValidationException if updated data is invalid
     * @throws NoteNotFoundException if note doesn't exist
     * @throws IOException if saving to storage fails
     */
    public Note updateNote(String title, String content, List<String> tags) 
            throws ValidationException, NoteNotFoundException, IOException {
        // Validate updated data before modification
        Note.validateContent(content);
        Note.validateTags(tags);
        
        // Find note in ArrayList by title (case-insensitive)
        Optional<Note> existingNote = getNote(title);
        if (!existingNote.isPresent()) {
            throw new NoteNotFoundException("Note with title '" + title + "' not found");
        }
        
        Note note = existingNote.get();
        
        // Update note content, tags, and modifiedAt timestamp
        note.setContent(content);
        note.setTags(tags);
        note.setModifiedAt(LocalDateTime.now());
        
        // Persist changes to JSON file
        saveToStorage();
        
        return note;
    }
    
    /**
     * Deletes a note by title (case-insensitive).
     * Finds note in ArrayList by title.
     * Removes note from ArrayList.
     * Calls storage.saveNotes() to persist changes.
     * 
     * @param title the title of the note to delete
     * @return true if note was deleted, false if note was not found
     * @throws IOException if saving to storage fails
     */
    public boolean deleteNote(String title) throws IOException {
        // Find note in ArrayList by title (case-insensitive)
        Optional<Note> noteToDelete = getNote(title);
        
        if (!noteToDelete.isPresent()) {
            return false;
        }
        
        // Remove note from ArrayList
        notes.remove(noteToDelete.get());
        
        // Persist changes to JSON file
        saveToStorage();
        
        return true;
    }
    
    /**
     * Loads notes from JSON file into ArrayList.
     * Updates nextId based on maximum id in loaded notes.
     * 
     * @throws IOException if loading from storage fails
     */
    public void loadFromStorage() throws IOException {
        NotesData data = storage.loadNotes();
        
        if (data != null) {
            this.notes = new ArrayList<>(data.getNotes());
            this.nextId = data.getNextId();
            
            // Update nextId based on maximum id in loaded notes
            // This ensures we don't reuse IDs if nextId in file is incorrect
            int maxId = notes.stream()
                    .map(Note::getId)
                    .filter(id -> id != null)
                    .max(Integer::compareTo)
                    .orElse(0);
            
            if (maxId >= nextId) {
                nextId = maxId + 1;
            }
        }
    }
    
    /**
     * Saves ArrayList to JSON file.
     * 
     * @throws IOException if saving to storage fails
     */
    public void saveToStorage() throws IOException {
        NotesData data = new NotesData(nextId, notes);
        storage.saveNotes(data);
    }
}
