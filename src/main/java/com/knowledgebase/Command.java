package com.knowledgebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a parsed command with its type and arguments.
 * 
 * Requirements: 4.3, 8.1
 */
public class Command {
    private CommandType action;
    private Map<String, String> args;
    
    /**
     * Creates a new Command with the specified action and arguments.
     * 
     * @param action the command type
     * @param args the command arguments
     */
    public Command(CommandType action, Map<String, String> args) {
        this.action = action;
        this.args = args != null ? args : new HashMap<>();
    }
    
    /**
     * Creates a new Command with the specified action and no arguments.
     * 
     * @param action the command type
     */
    public Command(CommandType action) {
        this(action, new HashMap<>());
    }
    
    /**
     * Gets the command action type.
     * 
     * @return the command type
     */
    public CommandType getAction() {
        return action;
    }
    
    /**
     * Gets the command arguments.
     * 
     * @return the arguments map
     */
    public Map<String, String> getArgs() {
        return args;
    }
    
    /**
     * Gets a specific argument value.
     * 
     * @param key the argument key
     * @return the argument value, or null if not present
     */
    public String getArg(String key) {
        return args.get(key);
    }
    
    /**
     * Checks if an argument exists.
     * 
     * @param key the argument key
     * @return true if the argument exists
     */
    public boolean hasArg(String key) {
        return args.containsKey(key);
    }
}
