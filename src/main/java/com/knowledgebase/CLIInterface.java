package com.knowledgebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Command-line interface for the Personal Knowledge Base application.
 * Provides a REPL loop for user interaction.
 * 
 * Requirements: 4.1, 4.2, 4.4, 4.5, 8.1
 */
public class CLIInterface {
    
    private static final Logger logger = Logger.getLogger(CLIInterface.class.getName());
    
    private Scanner scanner;
    private ApplicationController controller;
    private CommandParser commandParser;
    private boolean running;
    
    /**
     * Creates a new CLIInterface with the specified controller.
     * 
     * @param controller the application controller
     */
    public CLIInterface(ApplicationController controller) {
        this.scanner = new Scanner(System.in);
        this.controller = controller;
        this.commandParser = new CommandParser();
        this.running = false;
    }
    
    /**
     * Starts the REPL loop.
     * 
     * Requirements: 4.1, 4.4, 8.1
     */
    public void run() {
        running = true;
        displayWelcome();
        
        while (running) {
            try {
                System.out.print("\n> ");
                String input = scanner.nextLine();
                
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }
                
                Command command = commandParser.parseCommand(input);
                handleCommand(command);
                
            } catch (InvalidCommandException e) {
                displayError(e.getMessage());
            } catch (Exception e) {
                displayError("An error occurred: " + e.getMessage());
                logger.severe("Unexpected error in REPL: " + e.getMessage());
            }
        }
        
        // Cleanup before exit
        try {
            controller.cleanup();
            System.out.println("\nGoodbye!");
        } catch (IOException e) {
            displayError("Error saving data: " + e.getMessage());
        }
    }
    
    /**
     * Routes a command to the appropriate handler.
     * 
     * @param command the command to handle
     */
    private void handleCommand(Command command) {
        try {
            switch (command.getAction()) {
                case CREATE:
                    handleCreate();
                    break;
                case READ:
                    handleRead(command.getArg("title"));
                    break;
                case UPDATE:
                    handleUpdate(command.getArg("title"));
                    break;
                case DELETE:
                    handleDelete(command.getArg("title"));
                    break;
                case LIST:
                    handleList(command.getArg("tag"));
                    break;
                case SEARCH:
                    handleSearch(command.getArg("query"));
                    break;
                case LINKS:
                    handleLinks(command.getArg("title"));
                    break;
                case HELP:
                    displayHelp();
                    break;
                case EXIT:
                    running = false;
                    break;
                default:
                    displayError("Unknown command");
            }
        } catch (NoteNotFoundException e) {
            displayError("Note not found: " + e.getMessage());
        } catch (ValidationException e) {
            displayError("Validation error: " + e.getMessage());
        } catch (IOException e) {
            displayError("I/O error: " + e.getMessage());
            logger.severe("I/O error: " + e.getMessage());
        } catch (Exception e) {
            displayError("Error: " + e.getMessage());
            logger.severe("Error handling command: " + e.getMessage());
        }
    }
    
    /**
     * Handles the CREATE command.
     * Prompts for title, content, and tags, then creates a new note.
     * 
     * Requirements: 1.1, 4.2
     */
    private void handleCreate() throws ValidationException, IOException {
        System.out.println("\n=== Create New Note ===");
        
        // Get title
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        
        if (title.isEmpty()) {
            displayError("Title cannot be empty");
            return;
        }
        
        // Get content (multi-line)
        System.out.println("Content (enter '###' on a new line to finish):");
        StringBuilder contentBuilder = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("###")) {
                break;
            }
            if (contentBuilder.length() > 0) {
                contentBuilder.append("\n");
            }
            contentBuilder.append(line);
        }
        String content = contentBuilder.toString().trim();
        
        if (content.isEmpty()) {
            displayError("Content cannot be empty");
            return;
        }
        
        // Get tags
        System.out.print("Tags (comma-separated, optional): ");
        String tagsInput = scanner.nextLine().trim();
        List<String> tags = new ArrayList<>();
        if (!tagsInput.isEmpty()) {
            String[] tagArray = tagsInput.split(",");
            for (String tag : tagArray) {
                String trimmed = tag.trim();
                if (!trimmed.isEmpty()) {
                    tags.add(trimmed);
                }
            }
        }
        
        // Create the note
        Note note = controller.createNote(title, content, tags);
        System.out.println("\n✓ Note created successfully!");
        displayNote(note);
    }
    
    /**
     * Handles the READ command.
     * Displays a note by title.
     * 
     * Requirements: 1.2, 4.2
     * 
     * @param title the note title
     */
    private void handleRead(String title) {
        Optional<Note> noteOpt = controller.getNote(title);
        
        if (noteOpt.isPresent()) {
            displayNote(noteOpt.get());
        } else {
            displayError("Note not found: " + title);
        }
    }
    
    /**
     * Handles the UPDATE command.
     * Prompts for new content and tags, then updates the note.
     * 
     * Requirements: 1.3, 4.2
     * 
     * @param title the note title
     */
    private void handleUpdate(String title) throws NoteNotFoundException, ValidationException, IOException {
        // Check if note exists
        Optional<Note> noteOpt = controller.getNote(title);
        if (!noteOpt.isPresent()) {
            displayError("Note not found: " + title);
            return;
        }
        
        Note existingNote = noteOpt.get();
        System.out.println("\n=== Update Note: " + title + " ===");
        System.out.println("Current content:");
        System.out.println(existingNote.getContent());
        System.out.println();
        
        // Get new content
        System.out.println("New content (enter '###' on a new line to finish):");
        StringBuilder contentBuilder = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("###")) {
                break;
            }
            if (contentBuilder.length() > 0) {
                contentBuilder.append("\n");
            }
            contentBuilder.append(line);
        }
        String newContent = contentBuilder.toString().trim();
        
        if (newContent.isEmpty()) {
            displayError("Content cannot be empty");
            return;
        }
        
        // Get new tags
        System.out.print("Tags (comma-separated, current: " + existingNote.getTags() + "): ");
        String tagsInput = scanner.nextLine().trim();
        List<String> newTags = new ArrayList<>();
        if (!tagsInput.isEmpty()) {
            String[] tagArray = tagsInput.split(",");
            for (String tag : tagArray) {
                String trimmed = tag.trim();
                if (!trimmed.isEmpty()) {
                    newTags.add(trimmed);
                }
            }
        }
        
        // Update the note
        Note updatedNote = controller.updateNote(title, newContent, newTags);
        System.out.println("\n✓ Note updated successfully!");
        displayNote(updatedNote);
    }
    
    /**
     * Handles the DELETE command.
     * Confirms and deletes a note.
     * 
     * Requirements: 1.4, 4.2
     * 
     * @param title the note title
     */
    private void handleDelete(String title) throws IOException {
        System.out.print("Are you sure you want to delete '" + title + "'? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            boolean deleted = controller.deleteNote(title);
            if (deleted) {
                System.out.println("\n✓ Note deleted successfully!");
            } else {
                displayError("Note not found: " + title);
            }
        } else {
            System.out.println("Delete cancelled.");
        }
    }
    
    /**
     * Handles the LIST command.
     * Lists all notes or filters by tag.
     * 
     * Requirements: 2.1, 4.2
     * 
     * @param tagFilter optional tag filter
     */
    private void handleList(String tagFilter) {
        List<Note> notes;
        
        if (tagFilter != null && !tagFilter.isEmpty()) {
            notes = controller.listNotes(tagFilter);
            System.out.println("\n=== Notes with tag: " + tagFilter + " ===");
        } else {
            notes = controller.listNotes();
            System.out.println("\n=== All Notes ===");
        }
        
        if (notes.isEmpty()) {
            System.out.println("No notes found.");
        } else {
            displayNotes(notes);
        }
    }
    
    /**
     * Handles the SEARCH command.
     * Searches notes and displays ranked results.
     * 
     * Requirements: 3.1, 4.2
     * 
     * @param query the search query
     */
    private void handleSearch(String query) {
        List<SearchResult> results = controller.search(query, 10);
        
        System.out.println("\n=== Search Results for: " + query + " ===");
        
        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            displaySearchResults(results);
        }
    }
    
    /**
     * Handles the LINKS command.
     * Displays outgoing and incoming links for a note.
     * 
     * Requirements: 3.2, 4.2
     * 
     * @param title the note title
     */
    private void handleLinks(String title) throws NoteNotFoundException {
        List<Note>[] links = controller.getLinks(title);
        List<Note> outgoing = links[0];
        List<Note> incoming = links[1];
        
        displayLinks(outgoing, incoming, title);
    }
    
    /**
     * Displays the welcome message and available commands.
     * 
     * Requirements: 4.2
     */
    private void displayWelcome() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       Personal Knowledge Base - CLI Application           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        displayHelp();
    }
    
    /**
     * Displays help information with available commands.
     */
    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  create              - Create a new note");
        System.out.println("  read <title>        - Display a note");
        System.out.println("  update <title>      - Update a note");
        System.out.println("  delete <title>      - Delete a note");
        System.out.println("  list [tag]          - List all notes or filter by tag");
        System.out.println("  search <query>      - Search notes");
        System.out.println("  links <title>       - Show links for a note");
        System.out.println("  help                - Show this help message");
        System.out.println("  exit                - Exit the application");
    }
    
    /**
     * Displays a single note.
     * 
     * Requirements: 4.2
     * 
     * @param note the note to display
     */
    private void displayNote(Note note) {
        System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ " + note.getTitle());
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println(note.getContent());
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Tags: " + (note.getTags().isEmpty() ? "none" : note.getTags()));
        System.out.println("│ Created: " + note.getCreatedAt());
        System.out.println("│ Modified: " + note.getModifiedAt());
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }
    
    /**
     * Displays a list of notes.
     * 
     * Requirements: 4.2
     * 
     * @param notes the notes to display
     */
    private void displayNotes(List<Note> notes) {
        System.out.println("\nFound " + notes.size() + " note(s):\n");
        
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            System.out.println((i + 1) + ". " + note.getTitle());
            
            // Show preview of content (first 100 chars)
            String preview = note.getContent();
            if (preview.length() > 100) {
                preview = preview.substring(0, 100) + "...";
            }
            preview = preview.replace("\n", " ");
            System.out.println("   " + preview);
            
            // Show tags
            if (!note.getTags().isEmpty()) {
                System.out.println("   Tags: " + note.getTags());
            }
            System.out.println();
        }
    }
    
    /**
     * Displays search results with ranking and snippets.
     * 
     * Requirements: 4.2, 4.5
     * 
     * @param results the search results to display
     */
    private void displaySearchResults(List<SearchResult> results) {
        System.out.println("\nFound " + results.size() + " result(s):\n");
        
        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            Note note = result.getNote();
            
            System.out.println((i + 1) + ". " + note.getTitle() + " (score: " + 
                             String.format("%.2f", result.getScore()) + ")");
            System.out.println("   " + result.getSnippet());
            
            if (!note.getTags().isEmpty()) {
                System.out.println("   Tags: " + note.getTags());
            }
            System.out.println();
        }
    }
    
    /**
     * Displays outgoing and incoming links for a note.
     * 
     * Requirements: 4.2
     * 
     * @param outgoing the outgoing links
     * @param incoming the incoming links
     * @param noteTitle the title of the note
     */
    private void displayLinks(List<Note> outgoing, List<Note> incoming, String noteTitle) {
        System.out.println("\n=== Links for: " + noteTitle + " ===");
        
        System.out.println("\nOutgoing links (" + outgoing.size() + "):");
        if (outgoing.isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (Note note : outgoing) {
                System.out.println("  → " + note.getTitle());
            }
        }
        
        System.out.println("\nIncoming links (" + incoming.size() + "):");
        if (incoming.isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (Note note : incoming) {
                System.out.println("  ← " + note.getTitle());
            }
        }
    }
    
    /**
     * Displays an error message.
     * 
     * Requirements: 4.2, 8.1
     * 
     * @param message the error message
     */
    private void displayError(String message) {
        System.out.println("\n✗ Error: " + message);
    }
}
