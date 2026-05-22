package com.knowledgebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchEngine tokenization functionality.
 */
class SearchEngineTokenizeTest {
    
    private SearchEngine searchEngine;
    
    @BeforeEach
    void setUp() {
        searchEngine = new SearchEngine(new ArrayList<>());
    }
    
    @Test
    void testTokenize_BasicText() {
        String[] tokens = searchEngine.tokenize("Hello world");
        
        assertNotNull(tokens);
        assertEquals(2, tokens.length);
        assertEquals("hello", tokens[0]);
        assertEquals("world", tokens[1]);
    }
    
    @Test
    void testTokenize_Lowercase() {
        String[] tokens = searchEngine.tokenize("Java PROGRAMMING Language");
        
        assertNotNull(tokens);
        assertEquals(3, tokens.length);
        assertEquals("java", tokens[0]);
        assertEquals("programming", tokens[1]);
        assertEquals("language", tokens[2]);
    }
    
    @Test
    void testTokenize_RemovesPunctuation() {
        String[] tokens = searchEngine.tokenize("Hello, world! How are you?");
        
        assertNotNull(tokens);
        assertEquals(5, tokens.length);
        assertEquals("hello", tokens[0]);
        assertEquals("world", tokens[1]);
        assertEquals("how", tokens[2]);
        assertEquals("are", tokens[3]);
        assertEquals("you", tokens[4]);
    }
    
    @Test
    void testTokenize_EmptyString() {
        String[] tokens = searchEngine.tokenize("");
        
        assertNotNull(tokens);
        assertEquals(0, tokens.length);
    }
    
    @Test
    void testTokenize_NullString() {
        String[] tokens = searchEngine.tokenize(null);
        
        assertNotNull(tokens);
        assertEquals(0, tokens.length);
    }
    
    @Test
    void testTokenize_WhitespaceOnly() {
        String[] tokens = searchEngine.tokenize("   \t\n  ");
        
        assertNotNull(tokens);
        assertEquals(0, tokens.length);
    }
    
    @Test
    void testTokenize_MultipleSpaces() {
        String[] tokens = searchEngine.tokenize("hello    world");
        
        assertNotNull(tokens);
        assertEquals(2, tokens.length);
        assertEquals("hello", tokens[0]);
        assertEquals("world", tokens[1]);
    }
    
    @Test
    void testTokenize_SpecialCharacters() {
        String[] tokens = searchEngine.tokenize("hello@world#test$value");
        
        assertNotNull(tokens);
        assertTrue(tokens.length >= 4, "Should split on special characters");
    }
    
    @Test
    void testTokenize_Numbers() {
        String[] tokens = searchEngine.tokenize("Java 11 and Java 17");
        
        assertNotNull(tokens);
        assertEquals(5, tokens.length);
        assertEquals("java", tokens[0]);
        assertEquals("11", tokens[1]);
        assertEquals("and", tokens[2]);
        assertEquals("java", tokens[3]);
        assertEquals("17", tokens[4]);
    }
    
    @Test
    void testTokenize_Hyphens() {
        String[] tokens = searchEngine.tokenize("full-text search");
        
        assertNotNull(tokens);
        assertEquals(3, tokens.length);
        assertEquals("full", tokens[0]);
        assertEquals("text", tokens[1]);
        assertEquals("search", tokens[2]);
    }
    
    @Test
    void testTokenize_Apostrophes() {
        String[] tokens = searchEngine.tokenize("don't can't won't");
        
        assertNotNull(tokens);
        // Apostrophes are treated as separators
        assertTrue(tokens.length >= 3);
    }
    
    @Test
    void testTokenize_Unicode() {
        String[] tokens = searchEngine.tokenize("café naïve");
        
        assertNotNull(tokens);
        // Should handle unicode characters
        assertTrue(tokens.length >= 2);
    }
    
    @Test
    void testTokenize_MixedContent() {
        String[] tokens = searchEngine.tokenize("Java is a programming language. Version 11+!");
        
        assertNotNull(tokens);
        assertTrue(tokens.length >= 6);
        assertEquals("java", tokens[0]);
        assertEquals("is", tokens[1]);
        assertEquals("a", tokens[2]);
        assertEquals("programming", tokens[3]);
        assertEquals("language", tokens[4]);
    }
    
    @Test
    void testTokenize_SingleWord() {
        String[] tokens = searchEngine.tokenize("hello");
        
        assertNotNull(tokens);
        assertEquals(1, tokens.length);
        assertEquals("hello", tokens[0]);
    }
    
    @Test
    void testTokenize_SingleCharacter() {
        String[] tokens = searchEngine.tokenize("a");
        
        assertNotNull(tokens);
        assertEquals(1, tokens.length);
        assertEquals("a", tokens[0]);
    }
    
    @Test
    void testTokenize_PunctuationOnly() {
        String[] tokens = searchEngine.tokenize("!@#$%^&*()");
        
        assertNotNull(tokens);
        assertEquals(0, tokens.length, "Should return empty array for punctuation only");
    }
    
    @Test
    void testTokenize_NewlinesAndTabs() {
        String[] tokens = searchEngine.tokenize("hello\nworld\ttesting");
        
        assertNotNull(tokens);
        assertEquals(3, tokens.length);
        assertEquals("hello", tokens[0]);
        assertEquals("world", tokens[1]);
        assertEquals("testing", tokens[2]);
    }
    
    @Test
    void testTokenize_RepeatedWords() {
        String[] tokens = searchEngine.tokenize("java java java");
        
        assertNotNull(tokens);
        assertEquals(3, tokens.length);
        // Should not deduplicate - returns all occurrences
        assertEquals("java", tokens[0]);
        assertEquals("java", tokens[1]);
        assertEquals("java", tokens[2]);
    }
}
