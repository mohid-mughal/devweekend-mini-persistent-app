package com.knowledgebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchEngine TF-IDF calculation.
 */
class SearchEngineTFIDFTest {
    
    private ArrayList<Note> notes;
    private SearchEngine searchEngine;
    
    @BeforeEach
    void setUp() {
        notes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Create test notes with known content for TF-IDF testing
        Note note1 = new Note(1, "Doc1", 
                             "java java python",
                             Arrays.asList("test"),
                             now, now);
        
        Note note2 = new Note(2, "Doc2",
                             "java python python",
                             Arrays.asList("test"),
                             now, now);
        
        Note note3 = new Note(3, "Doc3",
                             "ruby ruby ruby",
                             Arrays.asList("test"),
                             now, now);
        
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        
        searchEngine = new SearchEngine(notes);
    }
    
    @Test
    void testCalculateTFIDF_BasicScore() {
        String text = "java java python";
        String[] queryTerms = {"java"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Score should be positive for matching term");
    }
    
    @Test
    void testCalculateTFIDF_HigherFrequencyHigherScore() {
        // Text with "java" appearing twice
        String text1 = "java java python";
        String[] queryTerms = {"java"};
        double score1 = searchEngine.calculateTFIDF(text1, queryTerms);
        
        // Text with "java" appearing once
        String text2 = "java python python";
        double score2 = searchEngine.calculateTFIDF(text2, queryTerms);
        
        assertTrue(score1 > score2, 
                  "Text with higher term frequency should have higher score");
    }
    
    @Test
    void testCalculateTFIDF_MultipleQueryTerms() {
        String text = "java python ruby";
        String[] queryTerms = {"java", "python"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Score should be positive for multiple matching terms");
    }
    
    @Test
    void testCalculateTFIDF_NoMatchingTerms() {
        String text = "java python";
        String[] queryTerms = {"ruby"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertEquals(0.0, score, "Score should be zero for non-matching terms");
    }
    
    @Test
    void testCalculateTFIDF_EmptyText() {
        String text = "";
        String[] queryTerms = {"java"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertEquals(0.0, score, "Score should be zero for empty text");
    }
    
    @Test
    void testCalculateTFIDF_NullText() {
        String[] queryTerms = {"java"};
        
        double score = searchEngine.calculateTFIDF(null, queryTerms);
        
        assertEquals(0.0, score, "Score should be zero for null text");
    }
    
    @Test
    void testCalculateTFIDF_EmptyQueryTerms() {
        String text = "java python";
        String[] queryTerms = {};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertEquals(0.0, score, "Score should be zero for empty query terms");
    }
    
    @Test
    void testCalculateTFIDF_NullQueryTerms() {
        String text = "java python";
        
        double score = searchEngine.calculateTFIDF(text, null);
        
        assertEquals(0.0, score, "Score should be zero for null query terms");
    }
    
    @Test
    void testCalculateTFIDF_CaseInsensitive() {
        String text = "Java JAVA java";
        String[] queryTerms = {"java"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should match terms case-insensitively");
    }
    
    @Test
    void testCalculateTFIDF_RareTermHigherIDF() {
        // "ruby" appears in only 1 document (Doc3)
        // "java" appears in 2 documents (Doc1, Doc2)
        // Rare terms should have higher IDF component
        
        String text = "ruby java";
        String[] rubyQuery = {"ruby"};
        String[] javaQuery = {"java"};
        
        double rubyScore = searchEngine.calculateTFIDF(text, rubyQuery);
        double javaScore = searchEngine.calculateTFIDF(text, javaQuery);
        
        // Both terms appear once in the text, but ruby is rarer across documents
        assertTrue(rubyScore > javaScore, 
                  "Rarer terms should have higher IDF and thus higher score");
    }
    
    @Test
    void testCalculateTFIDF_NullTermInArray() {
        String text = "java python";
        String[] queryTerms = {"java", null, "python"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle null terms in array gracefully");
    }
    
    @Test
    void testCalculateTFIDF_EmptyTermInArray() {
        String text = "java python";
        String[] queryTerms = {"java", "", "python"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle empty terms in array gracefully");
    }
    
    @Test
    void testCalculateTFIDF_SpecialCharactersInText() {
        String text = "java! python? ruby.";
        String[] queryTerms = {"java", "python"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle special characters in text");
    }
    
    @Test
    void testCalculateTFIDF_SpecialCharactersInQuery() {
        String text = "java python ruby";
        String[] queryTerms = {"java!", "python?"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle special characters in query terms");
    }
    
    @Test
    void testCalculateTFIDF_SingleWordText() {
        String text = "java";
        String[] queryTerms = {"java"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle single word text");
    }
    
    @Test
    void testCalculateTFIDF_SingleQueryTerm() {
        String text = "java python ruby";
        String[] queryTerms = {"java"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle single query term");
    }
    
    @Test
    void testCalculateTFIDF_AllTermsMatch() {
        String text = "java python ruby";
        String[] queryTerms = {"java", "python", "ruby"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle all terms matching");
    }
    
    @Test
    void testCalculateTFIDF_PartialMatch() {
        String text = "java python";
        String[] queryTerms = {"java", "ruby"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle partial matches");
    }
    
    @Test
    void testCalculateTFIDF_EmptyNotesCollection() {
        SearchEngine emptyEngine = new SearchEngine(new ArrayList<>());
        String text = "java python";
        String[] queryTerms = {"java"};
        
        double score = emptyEngine.calculateTFIDF(text, queryTerms);
        
        // With no documents, IDF calculation may return 0 or handle gracefully
        assertTrue(score >= 0, "Should handle empty notes collection");
    }
    
    @Test
    void testCalculateTFIDF_ScoreIsNonNegative() {
        String text = "java python ruby";
        String[] queryTerms = {"java", "python", "ruby", "nonexistent"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score >= 0, "Score should never be negative");
    }
    
    @Test
    void testCalculateTFIDF_RepeatedQueryTerms() {
        String text = "java python";
        String[] queryTerms = {"java", "java", "java"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle repeated query terms");
    }
    
    @Test
    void testCalculateTFIDF_LongText() {
        String text = "java ".repeat(100) + "python";
        String[] queryTerms = {"java"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle long text");
    }
    
    @Test
    void testCalculateTFIDF_ManyQueryTerms() {
        String text = "java python ruby javascript typescript";
        String[] queryTerms = {"java", "python", "ruby", "javascript", "typescript"};
        
        double score = searchEngine.calculateTFIDF(text, queryTerms);
        
        assertTrue(score > 0, "Should handle many query terms");
    }
}
