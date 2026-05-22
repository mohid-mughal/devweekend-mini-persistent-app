package com.knowledgebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoteManager class.
 * Tests CRUD operations, validation, and persistence.
 */
class NoteManagerTest {
    
    private NoteManager noteManager;
    private StorageLayer storage;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() throws IOException {
        storage = new StorageLayer(tempDir.toString());
        storage.initializeStorage();
        noteManager = new NoteManager(storage);
    }
    
    // Subtask 4.1: Create Note Tests
    
    @Test
    void testCreateNote_ValidData_Success() throws ValidationException, IOException {
        String title = "Test Note";
        String content = "This is test content";
        List<String> tags = Arrays.asList("test", "example");
        
        Note created = noteManager.createNote(title, content, tags);
        
        assertNotNull(created);
        assertEquals(1, created.getId());
        assertEquals(title, created.getTitle());
        assertEquals(content, created.getContent());
        assertEquals(tags, created.getTags());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getModifiedAt());
    }
    
    @Test
    void testCreateNote_DuplicateTitle_ThrowsValidationException() throws ValidationException, IOException {
        String title = "Duplicate Title";
        noteManager.createNote(title, "Content 1", null);
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.createNote(title, "Content 2", null);
        });
        
        assertTrue(exception.getMessage().contains("unique"));
    }
    
    @Test
    void testCreateNote_DuplicateTitleCaseInsensitive_ThrowsValidationException() throws ValidationException, IOException {
        noteManager.createNote("Test Title", "Content 1", null);
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.createNote("test title", "Content 2", null);
        });
        
        assertTrue(exception.getMessage().contains("unique"));
    }
    
    @Test
    void testCreateNote_EmptyTitle_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.createNote("", "Content", null);
        });
        
        assertTrue(exception.getMessage().contains("empty"));
    }
    
    @Test
    void testCreateNote_NullTitle_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.createNote(null, "Content", null);
        });
        
        assertTrue(exception.getMessage().contains("null"));
    }
    
    @Test
    void testCreateNote_EmptyContent_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.createNote("Title", "", null);
        });
        
        assertTrue(exception.getMessage().contains("empty"));
    }
    
    @Test
    void testCreateNote_InvalidTags_ThrowsValidationException() {
        List<String> invalidTags = Arrays.asList("valid-tag", "invalid tag with spaces");
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.createNote("Title", "Content", invalidTags);
        });
        
        assertTrue(exception.getMessage().contains("alphanumeric"));
    }
    
    @Test
    void testCreateNote_PersistsToStorage() throws ValidationException, IOException {
        noteManager.createNote("Persistent Note", "Content", null);
        
        // Create new manager instance to load from storage
        NoteManager newManager = new NoteManager(storage);
        newManager.loadFromStorage();
        
        Optional<Note> loaded = newManager.getNote("Persistent Note");
        assertTrue(loaded.isPresent());
        assertEquals("Persistent Note", loaded.get().getTitle());
    }
    
    // Subtask 4.2: Get and List Notes Tests
    
    @Test
    void testGetNote_ExistingNote_ReturnsNote() throws ValidationException, IOException {
        noteManager.createNote("Find Me", "Content", null);
        
        Optional<Note> found = noteManager.getNote("Find Me");
        
        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getTitle());
    }
    
    @Test
    void testGetNote_CaseInsensitive_ReturnsNote() throws ValidationException, IOException {
        noteManager.createNote("Case Test", "Content", null);
        
        Optional<Note> found = noteManager.getNote("case test");
        
        assertTrue(found.isPresent());
        assertEquals("Case Test", found.get().getTitle());
    }
    
    @Test
    void testGetNote_NonExistentNote_ReturnsEmpty() {
        Optional<Note> found = noteManager.getNote("Does Not Exist");
        
        assertFalse(found.isPresent());
    }
    
    @Test
    void testGetNote_NullTitle_ReturnsEmpty() {
        Optional<Note> found = noteManager.getNote(null);
        
        assertFalse(found.isPresent());
    }
    
    @Test
    void testListNotes_EmptyList_ReturnsEmpty() {
        List<Note> notes = noteManager.listNotes();
        
        assertTrue(notes.isEmpty());
    }
    
    @Test
    void testListNotes_MultipleNotes_ReturnsAll() throws ValidationException, IOException {
        noteManager.createNote("Note 1", "Content 1", null);
        noteManager.createNote("Note 2", "Content 2", null);
        noteManager.createNote("Note 3", "Content 3", null);
        
        List<Note> notes = noteManager.listNotes();
        
        assertEquals(3, notes.size());
    }
    
    @Test
    void testListNotes_WithTagFilter_ReturnsFiltered() throws ValidationException, IOException {
        noteManager.createNote("Note 1", "Content 1", Arrays.asList("java", "programming"));
        noteManager.createNote("Note 2", "Content 2", Arrays.asList("python", "programming"));
        noteManager.createNote("Note 3", "Content 3", Arrays.asList("java", "testing"));
        
        List<Note> javaNotes = noteManager.listNotes("java");
        
        assertEquals(2, javaNotes.size());
        assertTrue(javaNotes.stream().allMatch(n -> n.getTags().contains("java")));
    }
    
    @Test
    void testListNotes_WithTagFilterCaseInsensitive_ReturnsFiltered() throws ValidationException, IOException {
        noteManager.createNote("Note 1", "Content 1", Arrays.asList("Java"));
        noteManager.createNote("Note 2", "Content 2", Arrays.asList("Python"));
        
        List<Note> javaNotes = noteManager.listNotes("java");
        
        assertEquals(1, javaNotes.size());
        assertEquals("Note 1", javaNotes.get(0).getTitle());
    }
    
    @Test
    void testListNotes_WithNonExistentTag_ReturnsEmpty() throws ValidationException, IOException {
        noteManager.createNote("Note 1", "Content 1", Arrays.asList("java"));
        
        List<Note> notes = noteManager.listNotes("python");
        
        assertTrue(notes.isEmpty());
    }
    
    // Subtask 4.3: Update Note Tests
    
    @Test
    void testUpdateNote_ValidData_Success() throws ValidationException, IOException, NoteNotFoundException {
        noteManager.createNote("Update Me", "Original Content", Arrays.asList("old"));
        
        Note updated = noteManager.updateNote("Update Me", "New Content", Arrays.asList("new"));
        
        assertEquals("New Content", updated.getContent());
        assertEquals(Arrays.asList("new"), updated.getTags());
        assertNotNull(updated.getModifiedAt());
    }
    
    @Test
    void testUpdateNote_CaseInsensitiveTitle_Success() throws ValidationException, IOException, NoteNotFoundException {
        noteManager.createNote("Update Me", "Original Content", null);
        
        Note updated = noteManager.updateNote("update me", "New Content", null);
        
        assertEquals("New Content", updated.getContent());
    }
    
    @Test
    void testUpdateNote_NonExistentNote_ThrowsNotFoundException() {
        NoteNotFoundException exception = assertThrows(NoteNotFoundException.class, () -> {
            noteManager.updateNote("Does Not Exist", "Content", null);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }
    
    @Test
    void testUpdateNote_InvalidContent_ThrowsValidationException() throws ValidationException, IOException {
        noteManager.createNote("Update Me", "Original Content", null);
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.updateNote("Update Me", "", null);
        });
        
        assertTrue(exception.getMessage().contains("empty"));
    }
    
    @Test
    void testUpdateNote_InvalidTags_ThrowsValidationException() throws ValidationException, IOException {
        noteManager.createNote("Update Me", "Original Content", null);
        
        List<String> invalidTags = Arrays.asList("invalid tag");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteManager.updateNote("Update Me", "Content", invalidTags);
        });
        
        assertTrue(exception.getMessage().contains("alphanumeric"));
    }
    
    @Test
    void testUpdateNote_PersistsToStorage() throws ValidationException, IOException, NoteNotFoundException {
        noteManager.createNote("Persistent Update", "Original", null);
        noteManager.updateNote("Persistent Update", "Updated", null);
        
        // Create new manager instance to load from storage
        NoteManager newManager = new NoteManager(storage);
        newManager.loadFromStorage();
        
        Optional<Note> loaded = newManager.getNote("Persistent Update");
        assertTrue(loaded.isPresent());
        assertEquals("Updated", loaded.get().getContent());
    }
    
    // Subtask 4.4: Delete Note Tests
    
    @Test
    void testDeleteNote_ExistingNote_ReturnsTrue() throws ValidationException, IOException {
        noteManager.createNote("Delete Me", "Content", null);
        
        boolean deleted = noteManager.deleteNote("Delete Me");
        
        assertTrue(deleted);
        assertFalse(noteManager.getNote("Delete Me").isPresent());
    }
    
    @Test
    void testDeleteNote_CaseInsensitive_ReturnsTrue() throws ValidationException, IOException {
        noteManager.createNote("Delete Me", "Content", null);
        
        boolean deleted = noteManager.deleteNote("delete me");
        
        assertTrue(deleted);
    }
    
    @Test
    void testDeleteNote_NonExistentNote_ReturnsFalse() {
        boolean deleted = noteManager.deleteNote("Does Not Exist");
        
        assertFalse(deleted);
    }
    
    @Test
    void testDeleteNote_PersistsToStorage() throws ValidationException, IOException {
        noteManager.createNote("Delete Me", "Content", null);
        noteManager.deleteNote("Delete Me");
        
        // Create new manager instance to load from storage
        NoteManager newManager = new NoteManager(storage);
        newManager.loadFromStorage();
        
        Optional<Note> loaded = newManager.getNote("Delete Me");
        assertFalse(loaded.isPresent());
    }
    
    // Subtask 4.5: Load and Save Storage Tests
    
    @Test
    void testLoadFromStorage_EmptyStorage_Success() throws IOException {
        noteManager.loadFromStorage();
        
        List<Note> notes = noteManager.listNotes();
        assertTrue(notes.isEmpty());
    }
    
    @Test
    void testLoadFromStorage_WithNotes_LoadsCorrectly() throws ValidationException, IOException {
        noteManager.createNote("Note 1", "Content 1", Arrays.asList("tag1"));
        noteManager.createNote("Note 2", "Content 2", Arrays.asList("tag2"));
        
        // Create new manager and load
        NoteManager newManager = new NoteManager(storage);
        newManager.loadFromStorage();
        
        List<Note> notes = newManager.listNotes();
        assertEquals(2, notes.size());
    }
    
    @Test
    void testLoadFromStorage_UpdatesNextId() throws ValidationException, IOException {
        noteManager.createNote("Note 1", "Content 1", null);
        noteManager.createNote("Note 2", "Content 2", null);
        
        // Create new manager and load
        NoteManager newManager = new NoteManager(storage);
        newManager.loadFromStorage();
        
        // Create a new note - should have ID 3
        Note newNote = newManager.createNote("Note 3", "Content 3", null);
        assertEquals(3, newNote.getId());
    }
    
    @Test
    void testSaveToStorage_CreatesValidJSON() throws ValidationException, IOException {
        noteManager.createNote("Test Note", "Test Content", Arrays.asList("test"));
        
        // Verify we can load it back
        NoteManager newManager = new NoteManager(storage);
        newManager.loadFromStorage();
        
        Optional<Note> loaded = newManager.getNote("Test Note");
        assertTrue(loaded.isPresent());
        assertEquals("Test Note", loaded.get().getTitle());
        assertEquals("Test Content", loaded.get().getContent());
        assertEquals(Arrays.asList("test"), loaded.get().getTags());
    }
    
    // Integration Tests
    
    @Test
    void testCRUDWorkflow_CompleteLifecycle() throws ValidationException, IOException, NoteNotFoundException {
        // Create
        Note created = noteManager.createNote("Workflow Test", "Initial Content", Arrays.asList("test"));
        assertEquals(1, created.getId());
        
        // Read
        Optional<Note> read = noteManager.getNote("Workflow Test");
        assertTrue(read.isPresent());
        assertEquals("Initial Content", read.get().getContent());
        
        // Update
        Note updated = noteManager.updateNote("Workflow Test", "Updated Content", Arrays.asList("updated"));
        assertEquals("Updated Content", updated.getContent());
        
        // List
        List<Note> notes = noteManager.listNotes();
        assertEquals(1, notes.size());
        
        // Delete
        boolean deleted = noteManager.deleteNote("Workflow Test");
        assertTrue(deleted);
        
        // Verify deletion
        Optional<Note> afterDelete = noteManager.getNote("Workflow Test");
        assertFalse(afterDelete.isPresent());
    }
    
    @Test
    void testMultipleNotes_IndependentOperations() throws ValidationException, IOException, NoteNotFoundException {
        // Create multiple notes
        noteManager.createNote("Note A", "Content A", Arrays.asList("a"));
        noteManager.createNote("Note B", "Content B", Arrays.asList("b"));
        noteManager.createNote("Note C", "Content C", Arrays.asList("c"));
        
        // Update one note
        noteManager.updateNote("Note B", "Updated B", Arrays.asList("b", "updated"));
        
        // Delete one note
        noteManager.deleteNote("Note C");
        
        // Verify state
        List<Note> notes = noteManager.listNotes();
        assertEquals(2, notes.size());
        
        Optional<Note> noteB = noteManager.getNote("Note B");
        assertTrue(noteB.isPresent());
        assertEquals("Updated B", noteB.get().getContent());
        
        Optional<Note> noteC = noteManager.getNote("Note C");
        assertFalse(noteC.isPresent());
    }
}
