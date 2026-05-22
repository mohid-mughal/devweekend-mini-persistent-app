package com.knowledgebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Application controller that coordinates between all components.
 * Wires together StorageLayer, NoteManager, SearchEngine, and LinkManager.
 * 
 * Requirements: 9.1
 */
public class ApplicationController {
    
    private static final Logger logger = Logger.getLogger(ApplicationController.class.getName());
    
    private StorageLayer storage;
    private NoteManager noteManager;
    private SearchEngine searchEngine;
    private LinkManager linkManager;
    
    /**
     * Creates a new ApplicationController with the specified data directory.
     * Initializes all components and loads data from storage.
     * 
     * @param dataDirectory the directory for storing data files
     * @throws IOException if initialization or loading fails
     */
    public ApplicationController(String dataDirectory) throws IOException {
        // Initialize storage layer
        this.storage = new StorageLayer(dataDirectory);
        storage.initializeStorage();
        
        // Initialize managers
        this.noteManager = new NoteManager(storage);
        this.linkManager = new LinkManager();
        
        // Load data from storage
        loadData();
        
        // Initialize search engine with loaded notes
        this.searchEngine = new SearchEngine(noteManager.listNotes());
        
        logger.info("ApplicationController initialized with data directory: " + dataDirectory);
    }
    
    /**
     * Loads notes and links from storage into memory.
     * 
     * @throws IOException if loading fails
     */
    private void loadData() throws IOException {
        // Load notes
        noteManager.loadFromStorage();
        
        // Load links
        LinksData linksData = storage.loadLinks();
        linkManager.setLinks(new ArrayList<>(linksData.getLinks()));
        
        logger.info("Loaded " + noteManager.listNotes().size() + " notes and " + 
                    linkManager.getLinks().size() + " links from storage");
    }
    
    /**
     * Creates a new note with the specified title, content, and tags.
     * Also updates links and resolves orphaned links.
     * 
     * Requirements: 1.1, 3.3, 3.4
     * 
     * @param title the note title
     * @param content the note content
     * @param tags the note tags
     * @return the created Note object
     * @throws ValidationException if validation fails
     * @throws IOException if saving fails
     */
    public Note createNote(String title, String content, List<String> tags) 
            throws ValidationException, IOException {
        // Create the note
        Note note = noteManager.createNote(title, content, tags);
        
        // Update links for the new note
        linkManager.updateLinks(note.getId(), content, new ArrayList<>(noteManager.listNotes()), storage);
        
        // Resolve orphaned links (other notes that referenced this title before it existed)
        linkManager.resolveOrphanedLinks(title, note.getId(), storage);
        
        // Refresh search engine with updated notes
        this.searchEngine = new SearchEngine(noteManager.listNotes());
        
        logger.info("Created note: " + title + " (ID: " + note.getId() + ")");
        return note;
    }
    
    /**
     * Gets a note by title.
     * 
     * Requirements: 1.2
     * 
     * @param title the note title
     * @return Optional containing the note if found
     */
    public Optional<Note> getNote(String title) {
        return noteManager.getNote(title);
    }
    
    /**
     * Updates an existing note with new content and tags.
     * Also updates links.
     * 
     * Requirements: 1.3, 3.3, 3.4
     * 
     * @param title the note title
     * @param newContent the new content
     * @param newTags the new tags
     * @return the updated Note object
     * @throws NoteNotFoundException if the note doesn't exist
     * @throws ValidationException if validation fails
     * @throws IOException if saving fails
     */
    public Note updateNote(String title, String newContent, List<String> newTags) 
            throws NoteNotFoundException, ValidationException, IOException {
        // Update the note
        Note note = noteManager.updateNote(title, newContent, newTags);
        
        // Update links for the modified note
        linkManager.updateLinks(note.getId(), newContent, new ArrayList<>(noteManager.listNotes()), storage);
        
        // Refresh search engine with updated notes
        this.searchEngine = new SearchEngine(noteManager.listNotes());
        
        logger.info("Updated note: " + title + " (ID: " + note.getId() + ")");
        return note;
    }
    
    /**
     * Deletes a note by title.
     * Also deletes all associated links.
     * 
     * Requirements: 1.4, 3.3
     * 
     * @param title the note title
     * @return true if the note was deleted, false if not found
     * @throws IOException if saving fails
     */
    public boolean deleteNote(String title) throws IOException {
        // Find the note to get its ID
        Optional<Note> noteOpt = noteManager.getNote(title);
        if (!noteOpt.isPresent()) {
            return false;
        }
        
        int noteId = noteOpt.get().getId();
        
        // Delete all links associated with this note
        linkManager.deleteLinksForNote(noteId, storage);
        
        // Delete the note
        boolean deleted = noteManager.deleteNote(title);
        
        if (deleted) {
            // Refresh search engine with updated notes
            this.searchEngine = new SearchEngine(noteManager.listNotes());
            logger.info("Deleted note: " + title + " (ID: " + noteId + ")");
        }
        
        return deleted;
    }
    
    /**
     * Lists all notes.
     * 
     * Requirements: 2.1
     * 
     * @return list of all notes
     */
    public List<Note> listNotes() {
        return noteManager.listNotes();
    }
    
    /**
     * Lists notes filtered by tag.
     * 
     * Requirements: 2.1
     * 
     * @param tag the tag to filter by
     * @return list of notes with the specified tag
     */
    public List<Note> listNotes(String tag) {
        return noteManager.listNotes(tag);
    }
    
    /**
     * Searches notes using the specified query.
     * 
     * Requirements: 3.1
     * 
     * @param query the search query
     * @param limit the maximum number of results
     * @return list of search results
     */
    public List<SearchResult> search(String query, int limit) {
        return searchEngine.search(query, limit);
    }
    
    /**
     * Gets outgoing and incoming links for a note.
     * 
     * Requirements: 3.2
     * 
     * @param title the note title
     * @return array with [0] = outgoing links, [1] = incoming links
     * @throws NoteNotFoundException if the note doesn't exist
     */
    public List<Note>[] getLinks(String title) throws NoteNotFoundException {
        Optional<Note> noteOpt = noteManager.getNote(title);
        if (!noteOpt.isPresent()) {
            throw new NoteNotFoundException("Note not found: " + title);
        }
        
        Note note = noteOpt.get();
        List<Note> outgoing = linkManager.getOutgoingLinks(note.getId(), new ArrayList<>(noteManager.listNotes()));
        List<Note> incoming = linkManager.getIncomingLinks(note.getId(), new ArrayList<>(noteManager.listNotes()));
        
        @SuppressWarnings("unchecked")
        List<Note>[] result = new List[2];
        result[0] = outgoing;
        result[1] = incoming;
        
        return result;
    }
    
    /**
     * Ensures all data is saved before application exit.
     * 
     * @throws IOException if saving fails
     */
    public void cleanup() throws IOException {
        // Save notes
        noteManager.saveToStorage();
        
        // Save links
        LinksData linksData = new LinksData(linkManager.getLinks());
        storage.saveLinks(linksData);
        
        logger.info("Application cleanup completed");
    }
}
