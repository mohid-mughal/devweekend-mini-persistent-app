package com.knowledgebase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Storage layer for handling JSON file-based persistence.
 * Manages reading and writing notes and links to JSON files with atomic write operations.
 * 
 * Requirements: 7.1, 7.2, 7.3, 2.4, 2.5
 */
public class StorageLayer {
    
    private final String dataDirectory;
    private final String notesFile;
    private final String linksFile;
    private final Gson gson;
    
    /**
     * Creates a new StorageLayer with the specified data directory.
     * 
     * @param dataDirectory the directory where JSON files will be stored
     */
    public StorageLayer(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.notesFile = dataDirectory + File.separator + "notes.json";
        this.linksFile = dataDirectory + File.separator + "links.json";
        
        // Configure Gson with LocalDateTime adapter
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * Initializes the storage system by creating the data directory and JSON files if they don't exist.
     * 
     * @throws IOException if directory or file creation fails
     */
    public void initializeStorage() throws IOException {
        // Create data directory if it doesn't exist
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create data directory: " + dataDirectory);
            }
        }
        
        // Create notes.json if it doesn't exist
        File notesFileObj = new File(notesFile);
        if (!notesFileObj.exists()) {
            NotesData emptyNotes = new NotesData();
            saveNotes(emptyNotes);
        }
        
        // Create links.json if it doesn't exist
        File linksFileObj = new File(linksFile);
        if (!linksFileObj.exists()) {
            LinksData emptyLinks = new LinksData();
            saveLinks(emptyLinks);
        }
    }
    
    /**
     * Loads notes from the JSON file.
     * 
     * @return NotesData object containing all notes and nextId
     * @throws IOException if file reading fails
     * @throws JsonSyntaxException if JSON is malformed
     */
    public NotesData loadNotes() throws IOException {
        File file = new File(notesFile);
        if (!file.exists()) {
            return new NotesData();
        }
        
        try (FileReader reader = new FileReader(file)) {
            NotesData data = gson.fromJson(reader, NotesData.class);
            return data != null ? data : new NotesData();
        }
    }
    
    /**
     * Saves notes to the JSON file using atomic write pattern.
     * Writes to a temporary file first, then renames to ensure atomicity.
     * 
     * @param notesData the NotesData object to save
     * @throws IOException if file writing fails
     */
    public void saveNotes(NotesData notesData) throws IOException {
        String tempFile = notesFile + ".tmp";
        
        // Write to temporary file
        try (FileWriter writer = new FileWriter(tempFile)) {
            gson.toJson(notesData, writer);
            writer.flush();
        }
        
        // Atomic rename: replace old file with new file
        Path source = Paths.get(tempFile);
        Path target = Paths.get(notesFile);
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }
    
    /**
     * Loads links from the JSON file.
     * 
     * @return LinksData object containing all links
     * @throws IOException if file reading fails
     * @throws JsonSyntaxException if JSON is malformed
     */
    public LinksData loadLinks() throws IOException {
        File file = new File(linksFile);
        if (!file.exists()) {
            return new LinksData();
        }
        
        try (FileReader reader = new FileReader(file)) {
            LinksData data = gson.fromJson(reader, LinksData.class);
            return data != null ? data : new LinksData();
        }
    }
    
    /**
     * Saves links to the JSON file using atomic write pattern.
     * Writes to a temporary file first, then renames to ensure atomicity.
     * 
     * @param linksData the LinksData object to save
     * @throws IOException if file writing fails
     */
    public void saveLinks(LinksData linksData) throws IOException {
        String tempFile = linksFile + ".tmp";
        
        // Write to temporary file
        try (FileWriter writer = new FileWriter(tempFile)) {
            gson.toJson(linksData, writer);
            writer.flush();
        }
        
        // Atomic rename: replace old file with new file
        Path source = Paths.get(tempFile);
        Path target = Paths.get(linksFile);
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }
    
    /**
     * Checks the integrity of JSON files by attempting to read and parse them.
     * 
     * @return true if both JSON files are valid and readable, false otherwise
     */
    public boolean checkIntegrity() {
        try {
            loadNotes();
            loadLinks();
            return true;
        } catch (IOException | JsonSyntaxException e) {
            return false;
        }
    }
    
    /**
     * Custom Gson TypeAdapter for LocalDateTime serialization/deserialization.
     * Uses ISO-8601 format for timestamp representation.
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(FORMATTER));
            }
        }
        
        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            String dateTimeString = in.nextString();
            return dateTimeString != null ? LocalDateTime.parse(dateTimeString, FORMATTER) : null;
        }
    }
}
