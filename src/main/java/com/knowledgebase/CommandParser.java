package com.knowledgebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses command strings into Command objects.
 * Supports commands: create, read, update, delete, list, search, links, exit, help
 * 
 * Requirements: 4.3, 8.1
 */
public class CommandParser {
    
    /**
     * Parses a command string into a Command object.
     * 
     * Command formats:
     * - create
     * - read <title>
     * - update <title>
     * - delete <title>
     * - list [tag]
     * - search <query>
     * - links <title>
     * - exit
     * - help
     * 
     * @param input the command string to parse
     * @return the parsed Command object
     * @throws InvalidCommandException if the command syntax is invalid
     */
    public Command parseCommand(String input) throws InvalidCommandException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidCommandException("Command cannot be empty");
        }
        
        String trimmed = input.trim();
        String[] parts = trimmed.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : "";
        
        Map<String, String> argMap = new HashMap<>();
        
        switch (commandWord) {
            case "create":
                return new Command(CommandType.CREATE, argMap);
                
            case "read":
                if (args.isEmpty()) {
                    throw new InvalidCommandException("READ command requires a note title. Usage: read <title>");
                }
                argMap.put("title", args);
                return new Command(CommandType.READ, argMap);
                
            case "update":
                if (args.isEmpty()) {
                    throw new InvalidCommandException("UPDATE command requires a note title. Usage: update <title>");
                }
                argMap.put("title", args);
                return new Command(CommandType.UPDATE, argMap);
                
            case "delete":
                if (args.isEmpty()) {
                    throw new InvalidCommandException("DELETE command requires a note title. Usage: delete <title>");
                }
                argMap.put("title", args);
                return new Command(CommandType.DELETE, argMap);
                
            case "list":
                if (!args.isEmpty()) {
                    argMap.put("tag", args);
                }
                return new Command(CommandType.LIST, argMap);
                
            case "search":
                if (args.isEmpty()) {
                    throw new InvalidCommandException("SEARCH command requires a query. Usage: search <query>");
                }
                argMap.put("query", args);
                return new Command(CommandType.SEARCH, argMap);
                
            case "links":
                if (args.isEmpty()) {
                    throw new InvalidCommandException("LINKS command requires a note title. Usage: links <title>");
                }
                argMap.put("title", args);
                return new Command(CommandType.LINKS, argMap);
                
            case "exit":
            case "quit":
                return new Command(CommandType.EXIT, argMap);
                
            case "help":
                return new Command(CommandType.HELP, argMap);
                
            default:
                throw new InvalidCommandException("Unknown command: " + commandWord + ". Type 'help' for available commands.");
        }
    }
}
