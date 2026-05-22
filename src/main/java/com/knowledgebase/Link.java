package com.knowledgebase;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a link relationship between two notes.
 * Links are created when a note contains wiki-style references [[Note Title]].
 * 
 * Requirements: 3.2
 */
public class Link {
    
    private int fromNoteId;
    private String toNoteTitle;
    private Integer toNoteId; // May be null if target note doesn't exist yet (orphaned link)
    private LocalDateTime createdAt;
    
    /**
     * Default constructor for Gson deserialization.
     */
    public Link() {
    }
    
    /**
     * Creates a new Link with all fields.
     * 
     * @param fromNoteId the ID of the note containing the link
     * @param toNoteTitle the title of the target note
     * @param toNoteId the ID of the target note (may be null if target doesn't exist)
     * @param createdAt the timestamp when the link was created
     */
    public Link(int fromNoteId, String toNoteTitle, Integer toNoteId, LocalDateTime createdAt) {
        this.fromNoteId = fromNoteId;
        this.toNoteTitle = toNoteTitle;
        this.toNoteId = toNoteId;
        this.createdAt = createdAt;
    }
    
    // Getters
    
    public int getFromNoteId() {
        return fromNoteId;
    }
    
    public String getToNoteTitle() {
        return toNoteTitle;
    }
    
    public Integer getToNoteId() {
        return toNoteId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    // Setters
    
    public void setFromNoteId(int fromNoteId) {
        this.fromNoteId = fromNoteId;
    }
    
    public void setToNoteTitle(String toNoteTitle) {
        this.toNoteTitle = toNoteTitle;
    }
    
    public void setToNoteId(Integer toNoteId) {
        this.toNoteId = toNoteId;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return fromNoteId == link.fromNoteId &&
               Objects.equals(toNoteTitle, link.toNoteTitle) &&
               Objects.equals(toNoteId, link.toNoteId) &&
               Objects.equals(createdAt, link.createdAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(fromNoteId, toNoteTitle, toNoteId, createdAt);
    }
    
    @Override
    public String toString() {
        return "Link{" +
               "fromNoteId=" + fromNoteId +
               ", toNoteTitle='" + toNoteTitle + '\'' +
               ", toNoteId=" + toNoteId +
               ", createdAt=" + createdAt +
               '}';
    }
}
