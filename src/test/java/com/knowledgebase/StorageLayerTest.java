package com.knowledgebase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StorageLayer class.
 * Tests JSON file initialization, atomic write pattern, integrity checks, and serialization.
 * 
 * Requirements: 7.2, 7.3, 8.5
 */
class StorageLayerTest {
    
    @TempDir
    Path tempDir;
    
    private StorageLayer storage;
    private String dataDirectory;
    
    @BeforeEach
    void setUp() {
        dataDirectory = tempDir.toString();
        storage = new StorageLayer(dataDirectory);
    }
    
    @Test
    void testInitializeStorage_CreatesDirectoryAndFiles() throws IOException {
        // Act
        storage.initializeStorage();
        
        // Assert
        File dir = new File(dataDirectory);
        assertTrue(dir.exists(), "Data directory should exist");
        assertTrue(dir.isDirectory(), "Data directory should be a directory");
        
        File notesFile = new File(dataDirectory + File.separator + "notes.json");
        assertTrue(notesFile.exists(), "notes.json should exist");
        
        File linksFile = new File(dataDirectory + File.separator + "links.json");
        assertTrue(linksFile.exists(), "links.json should exist");
    }
    
    @Test
    void testSaveAndLoadNotes_RoundTrip() throws IOException {
        // Arrange
        storage.initializeStorage();
        
        LocalDateTime now = LocalDateTime.now();
        Note note1 = new Note(1, "Test Note", "Test content", 
                              Arrays.asList("tag1", "tag2"), now, now);
        Note note2 = new Note(2, "Another Note", "More content", 
                              new ArrayList<>(), now, now);
        
        List<Note> notes = Arrays.asList(note1, note2);
        NotesData notesData = new NotesData(3, notes);
        
        // Act
        storage.saveNotes(notesData);
        NotesData loaded = storage.loadNotes();
        
        // Assert
        assertNotNull(loaded, "Loaded data should not be null");
        assertEquals(3, loaded.getNextId(), "NextId should match");
        assertEquals(2, loaded.getNotes().size(), "Should have 2 notes");
        
        Note loadedNote1 = loaded.getNotes().get(0);
        assertEquals("Test Note", loadedNote1.getTitle());
        assertEquals("Test content", loadedNote1.getContent());
        assertEquals(2, loadedNote1.getTags().size());
        assertTrue(loadedNote1.getTags().contains("tag1"));
        assertTrue(loadedNote1.getTags().contains("tag2"));
    }
    
    @Test
    void testSaveAndLoadLinks_RoundTrip() throws IOException {
        // Arrange
        storage.initializeStorage();
        
        LocalDateTime now = LocalDateTime.now();
        Link link1 = new Link(1, "Target Note", 2, now);
        Link link2 = new Link(2, "Another Target", null, now); // Orphaned link
        
        List<Link> links = Arrays.asList(link1, link2);
        LinksData linksData = new LinksData(links);
        
        // Act
        storage.saveLinks(linksData);
        LinksData loaded = storage.loadLinks();
        
        // Assert
        assertNotNull(loaded, "Loaded data should not be null");
        assertEquals(2, loaded.getLinks().size(), "Should have 2 links");
        
        Link loadedLink1 = loaded.getLinks().get(0);
        assertEquals(1, loadedLink1.getFromNoteId());
        assertEquals("Target Note", loadedLink1.getToNoteTitle());
        assertEquals(2, loadedLink1.getToNoteId());
        
        Link loadedLink2 = loaded.getLinks().get(1);
        assertEquals(2, loadedLink2.getFromNoteId());
        assertEquals("Another Target", loadedLink2.getToNoteTitle());
        assertNull(loadedLink2.getToNoteId(), "Orphaned link should have null toNoteId");
    }
    
    @Test
    void testAtomicWrite_TempFileIsRemoved() throws IOException {
        // Arrange
        storage.initializeStorage();
        NotesData notesData = new NotesData();
        
        // Act
        storage.saveNotes(notesData);
        
        // Assert
        File tempFile = new File(dataDirectory + File.separator + "notes.json.tmp");
        assertFalse(tempFile.exists(), "Temporary file should be removed after atomic write");
        
        File notesFile = new File(dataDirectory + File.separator + "notes.json");
        assertTrue(notesFile.exists(), "Final notes.json should exist");
    }
    
    @Test
    void testCheckIntegrity_ValidFiles() throws IOException {
        // Arrange
        storage.initializeStorage();
        
        // Act
        boolean isValid = storage.checkIntegrity();
        
        // Assert
        assertTrue(isValid, "Integrity check should pass for valid files");
    }
    
    @Test
    void testCheckIntegrity_CorruptedFile() throws IOException {
        // Arrange
        storage.initializeStorage();
        
        // Corrupt the notes.json file
        File notesFile = new File(dataDirectory + File.separator + "notes.json");
        try (FileWriter writer = new FileWriter(notesFile)) {
            writer.write("{ invalid json content ][");
        }
        
        // Act
        boolean isValid = storage.checkIntegrity();
        
        // Assert
        assertFalse(isValid, "Integrity check should fail for corrupted files");
    }
    
    @Test
    void testLoadNotes_NonExistentFile_ReturnsEmpty() throws IOException {
        // Act (don't initialize storage, so files don't exist)
        NotesData loaded = storage.loadNotes();
        
        // Assert
        assertNotNull(loaded, "Should return empty NotesData, not null");
        assertEquals(1, loaded.getNextId(), "Default nextId should be 1");
        assertTrue(loaded.getNotes().isEmpty(), "Notes list should be empty");
    }
    
    @Test
    void testLoadLinks_NonExistentFile_ReturnsEmpty() throws IOException {
        // Act (don't initialize storage, so files don't exist)
        LinksData loaded = storage.loadLinks();
        
        // Assert
        assertNotNull(loaded, "Should return empty LinksData, not null");
        assertTrue(loaded.getLinks().isEmpty(), "Links list should be empty");
    }
    
    @Test
    void testLocalDateTimeSerialization() throws IOException {
        // Arrange
        storage.initializeStorage();
        
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        Note note = new Note(1, "Time Test", "Content", new ArrayList<>(), 
                            specificTime, specificTime);
        
        NotesData notesData = new NotesData(2, Arrays.asList(note));
        
        // Act
        storage.saveNotes(notesData);
        NotesData loaded = storage.loadNotes();
        
        // Assert
        Note loadedNote = loaded.getNotes().get(0);
        assertEquals(specificTime, loadedNote.getCreatedAt(), 
                    "CreatedAt timestamp should be preserved");
        assertEquals(specificTime, loadedNote.getModifiedAt(), 
                    "ModifiedAt timestamp should be preserved");
    }
    
    @Test
    void testSaveNotes_OverwritesExistingFile() throws IOException {
        // Arrange
        storage.initializeStorage();
        
        NotesData firstData = new NotesData(2, Arrays.asList(
            new Note(1, "First", "Content", new ArrayList<>(), 
                    LocalDateTime.now(), LocalDateTime.now())
        ));
        
        NotesData secondData = new NotesData(3, Arrays.asList(
            new Note(1, "First", "Content", new ArrayList<>(), 
                    LocalDateTime.now(), LocalDateTime.now()),
            new Note(2, "Second", "More content", new ArrayList<>(), 
                    LocalDateTime.now(), LocalDateTime.now())
        ));
        
        // Act
        storage.saveNotes(firstData);
        storage.saveNotes(secondData);
        NotesData loaded = storage.loadNotes();
        
        // Assert
        assertEquals(3, loaded.getNextId(), "Should have updated nextId");
        assertEquals(2, loaded.getNotes().size(), "Should have 2 notes from second save");
    }
}
