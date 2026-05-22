package com.knowledgebase;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Main entry point for the Personal Knowledge Base application.
 * This CLI application enables users to create, organize, and retrieve notes
 * with advanced features including full-text search and automatic bidirectional linking.
 * 
 * Requirements: 5.1, 5.2, 8.1, 8.2
 */
public class Main {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String DEFAULT_DATA_DIR = System.getProperty("user.home") + "/.persistent-mini-app";
    
    public static void main(String[] args) {
        try {
            // Determine data directory
            String dataDirectory = DEFAULT_DATA_DIR;
            if (args.length > 0) {
                dataDirectory = args[0];
            }
            
            // Configure logging
            LoggingConfig.configure(dataDirectory);
            
            logger.info("Starting Personal Knowledge Base application");
            logger.info("Data directory: " + dataDirectory);
            
            // Initialize application controller
            ApplicationController controller = new ApplicationController(dataDirectory);
            
            // Start CLI interface
            CLIInterface cli = new CLIInterface(controller);
            cli.run();
            
            logger.info("Application exited normally");
            
        } catch (IOException e) {
            System.err.println("\n✗ Fatal Error: Failed to initialize application");
            System.err.println("  Reason: " + e.getMessage());
            System.err.println("  Please check that the data directory is accessible and you have write permissions.");
            logger.severe("Failed to initialize application: " + e.getMessage());
            System.exit(1);
            
        } catch (Exception e) {
            System.err.println("\n✗ Fatal Error: An unexpected error occurred");
            System.err.println("  Reason: " + e.getMessage());
            logger.severe("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
