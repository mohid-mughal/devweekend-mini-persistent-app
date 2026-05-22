package com.knowledgebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages bidirectional link relationships between notes.
 * Extracts wiki-style links [[Note Title]] from note content and maintains link records.
 * 
 * Requirements: 3.2, 3.3, 3.4
 */
public class LinkManager {
    
    private ArrayList<Link> links;
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");
    
    /**
     * Creates a new LinkManager with an empty links collection.
     */
    public LinkManager() {
        this.links = new ArrayList<>();
    }
    
    /**
     * Extracts wiki-style links [[Note Title]] from note content.
     * Handles edge cases:
     * - [[Unclosed brackets are ignored
     * - Nested [[brackets]] extract only the innermost content
     * - Empty [[]] are ignored
     * - Returns unique note titles only
     * 
     * @param content the note content to extract links from
     * @return List of unique note titles referenced in the content
     */
    public List<String> extractLinks(String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<String> uniqueTitles = new HashSet<>();
        Matcher matcher = LINK_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String title = matcher.group(1);
            
            // Skip empty links [[]]
            if (title == null || title.trim().isEmpty()) {
                continue;
            }
            
            // Trim whitespace and add to set (ensures uniqueness)
            uniqueTitles.add(title.trim());
        }
        
        return new ArrayList<>(uniqueTitles);
    }
    
    /**
     * Gets the links collection.
     * 
     * @return the ArrayList of links
     */
    public ArrayList<Link> getLinks() {
        return links;
    }
    
    /**
     * Sets the links collection.
     * 
     * @param links the ArrayList of links to set
     */
    public void setLinks(ArrayList<Link> links) {
        this.links = links != null ? links : new ArrayList<>();
    }
    
    /**
     * Updates links for a specific note by removing old links and creating new ones.
     * Extracts wiki-style links from content, resolves target note IDs, and persists changes.
     * 
     * Requirements: 3.3, 3.4
     * 
     * @param noteId the ID of the note whose links are being updated
     * @param content the note content to extract links from
     * @param allNotes the list of all notes for resolving target note IDs
     * @param storage the storage layer for persisting link changes
     * @throws IOException if saving links fails
     */
    public void updateLinks(int noteId, String content, ArrayList<Note> allNotes, StorageLayer storage) throws java.io.IOException {
        // Remove all existing links where fromNoteId matches
        links.removeIf(link -> link.getFromNoteId() == noteId);
        
        // Extract links from new content
        List<String> extractedTitles = extractLinks(content);
        
        // Create new Link objects for each extracted link
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (String toNoteTitle : extractedTitles) {
            // Resolve toNoteId by searching notes ArrayList (case-insensitive)
            Integer toNoteId = null;
            for (Note note : allNotes) {
                if (note.getTitle() != null && note.getTitle().equalsIgnoreCase(toNoteTitle)) {
                    toNoteId = note.getId();
                    break;
                }
            }
            
            // Create new Link object (toNoteId may be null if target note doesn't exist)
            Link newLink = new Link(noteId, toNoteTitle, toNoteId, now);
            links.add(newLink);
        }
        
        // Wrap links in LinksData object and save
        LinksData linksData = new LinksData(links);
        storage.saveLinks(linksData);
    }
    
    /**
     * Gets all notes that this note links to (outgoing links).
     * Filters links where fromNoteId matches and joins with allNotes to return Note objects.
     * Handles orphaned links (toNoteId is null) gracefully by skipping them.
     * 
     * Requirements: 3.2
     * 
     * @param noteId the ID of the note to get outgoing links for
     * @param allNotes the list of all notes for resolving Note objects
     * @return List of Note objects that this note links to
     */
    public List<Note> getOutgoingLinks(int noteId, ArrayList<Note> allNotes) {
        List<Note> outgoingNotes = new ArrayList<>();
        
        // Filter links where fromNoteId matches
        for (Link link : links) {
            if (link.getFromNoteId() == noteId) {
                // Skip orphaned links (toNoteId is null)
                if (link.getToNoteId() == null) {
                    continue;
                }
                
                // Join with allNotes to find the target Note object
                for (Note note : allNotes) {
                    if (note.getId() != null && note.getId().equals(link.getToNoteId())) {
                        outgoingNotes.add(note);
                        break;
                    }
                }
            }
        }
        
        return outgoingNotes;
    }
    
    /**
     * Gets all notes that link to this note (incoming links/backlinks).
     * Filters links where toNoteId matches and joins with allNotes to return Note objects.
     * Handles orphaned links (toNoteId is null) gracefully by skipping them.
     * 
     * Requirements: 3.2
     * 
     * @param noteId the ID of the note to get incoming links for
     * @param allNotes the list of all notes for resolving Note objects
     * @return List of Note objects that link to this note
     */
    public List<Note> getIncomingLinks(int noteId, ArrayList<Note> allNotes) {
        List<Note> incomingNotes = new ArrayList<>();
        
        // Filter links where toNoteId matches
        for (Link link : links) {
            // Skip orphaned links (toNoteId is null)
            if (link.getToNoteId() == null) {
                continue;
            }
            
            if (link.getToNoteId() == noteId) {
                // Join with allNotes to find the source Note object
                for (Note note : allNotes) {
                    if (note.getId() != null && note.getId() == link.getFromNoteId()) {
                        incomingNotes.add(note);
                        break;
                    }
                }
            }
        }
        
        return incomingNotes;
    }
    
    /**
     * Resolves orphaned links when a note is created.
     * Finds all links with matching toNoteTitle and null toNoteId, and updates them
     * to set the toNoteId to the newly created note's ID.
     * 
     * Requirements: 3.3
     * 
     * @param noteTitle the title of the newly created note
     * @param noteId the ID of the newly created note
     * @param storage the storage layer for persisting link changes
     * @throws IOException if saving links fails
     */
    public void resolveOrphanedLinks(String noteTitle, int noteId, StorageLayer storage) throws java.io.IOException {
        // Find all orphaned links with matching toNoteTitle (case-insensitive)
        for (Link link : links) {
            if (link.getToNoteId() == null && 
                link.getToNoteTitle() != null && 
                link.getToNoteTitle().equalsIgnoreCase(noteTitle)) {
                // Update the link to set toNoteId
                link.setToNoteId(noteId);
            }
        }
        
        // Wrap links in LinksData object and save
        LinksData linksData = new LinksData(links);
        storage.saveLinks(linksData);
    }
    
    /**
     * Deletes all links associated with a note.
     * Removes all links where fromNoteId or toNoteId matches the given noteId.
     * This is called when a note is deleted to maintain referential integrity.
     * 
     * Requirements: 3.3
     * 
     * @param noteId the ID of the note being deleted
     * @param storage the storage layer for persisting link changes
     * @throws IOException if saving links fails
     */
    public void deleteLinksForNote(int noteId, StorageLayer storage) throws java.io.IOException {
        // Remove all links where fromNoteId or toNoteId matches
        links.removeIf(link -> 
            link.getFromNoteId() == noteId || 
            (link.getToNoteId() != null && link.getToNoteId() == noteId)
        );
        
        // Wrap links in LinksData object and save
        LinksData linksData = new LinksData(links);
        storage.saveLinks(linksData);
    }
}
