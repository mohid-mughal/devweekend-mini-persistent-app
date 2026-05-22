package com.knowledgebase;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Configures the logging system for the application.
 * Logs to ~/.persistent-mini-app/app.log with rotation.
 * 
 * Requirements: 8.4
 */
public class LoggingConfig {
    
    private static boolean configured = false;
    
    /**
     * Configures the logging system.
     * Sets up file logging with rotation (5 files, 10MB each).
     * 
     * @param dataDirectory the data directory for log files
     * @throws IOException if log file setup fails
     */
    public static void configure(String dataDirectory) throws IOException {
        if (configured) {
            return;
        }
        
        // Create log directory if it doesn't exist
        File logDir = new File(dataDirectory);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        // Set up file handler with rotation
        String logFilePath = dataDirectory + File.separator + "app.log";
        FileHandler fileHandler = new FileHandler(
            logFilePath,
            10 * 1024 * 1024,  // 10MB per file
            5,                  // 5 files
            true                // append mode
        );
        
        // Set formatter
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        fileHandler.setLevel(Level.ALL);
        
        // Configure root logger
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(fileHandler);
        rootLogger.setLevel(Level.INFO);
        
        // Configure application loggers
        Logger appLogger = Logger.getLogger("com.knowledgebase");
        appLogger.setLevel(Level.INFO);
        
        configured = true;
        
        Logger.getLogger(LoggingConfig.class.getName()).info("Logging configured: " + logFilePath);
    }
}
