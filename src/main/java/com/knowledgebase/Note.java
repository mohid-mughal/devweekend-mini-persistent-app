package com.knowledgebase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a note in the Personal Knowledge Base.
 * Contains title, content, tags, and timestamps.
 */
public class Note {
    
    private Integer id;
    private String title;
    private String content;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    /**
     * Default constructor for Gson deserialization.
     */
    public Note() {
        this.tags = new ArrayList<>();
    }
    
    /**
     * Creates a new Note with all fields.
     * 
     * @param id the unique identifier
     * @param title the note title
     * @param content the note content
     * @param tags the list of tags
     * @param createdAt the creation timestamp
     * @param modifiedAt the last modification timestamp
     */
    public Note(Integer id, String title, String content, List<String> tags, 
                LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
    
    // Getters
    
    public Integer getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getContent() {
        return content;
    }
    
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
    
    // Setters
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id) &&
               Objects.equals(title, note.title) &&
               Objects.equals(content, note.content) &&
               Objects.equals(tags, note.tags) &&
               Objects.equals(createdAt, note.createdAt) &&
               Objects.equals(modifiedAt, note.modifiedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, tags, createdAt, modifiedAt);
    }
    
    @Override
    public String toString() {
        return "Note{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", content='" + (content != null && content.length() > 50 ? 
                   content.substring(0, 50) + "..." : content) + '\'' +
               ", tags=" + tags +
               ", createdAt=" + createdAt +
               ", modifiedAt=" + modifiedAt +
               '}';
    }
    
    // Static validation methods
    
    /**
     * Validates a note title.
     * 
     * @param title the title to validate
     * @throws ValidationException if the title is invalid
     */
    public static void validateTitle(String title) throws ValidationException {
        if (title == null) {
            throw new ValidationException("Title cannot be null");
        }
        
        if (title.isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
        
        // Check for leading/trailing whitespace
        if (!title.equals(title.trim())) {
            throw new ValidationException("Title cannot have leading or trailing whitespace");
        }
        
        if (title.length() > 200) {
            throw new ValidationException("Title cannot exceed 200 characters (current: " + title.length() + ")");
        }
    }
    
    /**
     * Validates a note title for uniqueness against existing titles.
     * 
     * @param title the title to validate
     * @param existingTitles list of existing note titles (case-insensitive comparison)
     * @throws ValidationException if the title is not unique
     */
    public static void validateTitleUniqueness(String title, List<String> existingTitles) throws ValidationException {
        if (title == null || existingTitles == null) {
            return;
        }
        
        String titleLower = title.toLowerCase();
        for (String existing : existingTitles) {
            if (existing != null && existing.toLowerCase().equals(titleLower)) {
                throw new ValidationException("Title must be unique (case-insensitive). A note with title '" + existing + "' already exists");
            }
        }
    }
    
    /**
     * Validates note content.
     * 
     * @param content the content to validate
     * @throws ValidationException if the content is invalid
     */
    public static void validateContent(String content) throws ValidationException {
        if (content == null) {
            throw new ValidationException("Content cannot be null");
        }
        
        if (content.isEmpty()) {
            throw new ValidationException("Content cannot be empty");
        }
        
        if (content.length() > 100000) {
            throw new ValidationException("Content cannot exceed 100,000 characters (current: " + content.length() + ")");
        }
    }
    
    /**
     * Validates a list of tags.
     * 
     * @param tags the tags to validate
     * @throws ValidationException if any tag is invalid
     */
    public static void validateTags(List<String> tags) throws ValidationException {
        if (tags == null) {
            return; // Tags are optional
        }
        
        if (tags.size() > 10) {
            throw new ValidationException("Cannot have more than 10 tags (current: " + tags.size() + ")");
        }
        
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);
            
            if (tag == null) {
                throw new ValidationException("Tag at index " + i + " cannot be null");
            }
            
            if (tag.isEmpty()) {
                throw new ValidationException("Tag at index " + i + " cannot be empty");
            }
            
            if (tag.length() > 50) {
                throw new ValidationException("Tag at index " + i + " cannot exceed 50 characters (current: " + tag.length() + ")");
            }
            
            // Validate alphanumeric plus hyphens
            if (!tag.matches("^[a-zA-Z0-9-]+$")) {
                throw new ValidationException("Tag at index " + i + " must contain only alphanumeric characters and hyphens (invalid: '" + tag + "')");
            }
        }
    }
    
    /**
     * Validates all note fields.
     * 
     * @param title the title to validate
     * @param content the content to validate
     * @param tags the tags to validate
     * @throws ValidationException if any field is invalid
     */
    public static void validateNote(String title, String content, List<String> tags) throws ValidationException {
        validateTitle(title);
        validateContent(content);
        validateTags(tags);
    }
}
