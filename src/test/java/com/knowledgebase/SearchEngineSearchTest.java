package com.knowledgebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchEngine search functionality with TF-IDF ranking.
 */
class SearchEngineSearchTest {
    
    private ArrayList<Note> notes;
    private SearchEngine searchEngine;
    
    @BeforeEach
    void setUp() {
        notes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Create test notes
        Note note1 = new Note(1, "Java Programming", 
                             "Java is a popular programming language. Java is used for enterprise applications.",
                             Arrays.asList("programming", "java"),
                             now, now);
        
        Note note2 = new Note(2, "Python Basics",
                             "Python is a simple programming language. Python is great for beginners.",
                             Arrays.asList("programming", "python"),
                             now, now);
        
        Note note3 = new Note(3, "Web Development",
                             "Web development involves HTML, CSS, and JavaScript. Modern web apps use frameworks.",
                             Arrays.asList("web", "development"),
                             now, now);
        
        Note note4 = new Note(4, "Java Advanced Topics",
                             "Advanced Java includes concurrency, streams, and design patterns.",
                             Arrays.asList("java", "advanced"),
                             now, now);
        
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        
        searchEngine = new SearchEngine(notes);
    }
    
    @Test
    void testSearch_BasicQuery() {
        List<SearchResult> results = searchEngine.search("java", 10);
        
        assertNotNull(results);
        assertEquals(2, results.size(), "Should find 2 notes containing 'java'");
        
        // Verify both Java notes are in results
        boolean hasJavaProgramming = false;
        boolean hasJavaAdvanced = false;
        
        for (SearchResult result : results) {
            String title = result.getNote().getTitle();
            if (title.equals("Java Programming")) hasJavaProgramming = true;
            if (title.equals("Java Advanced Topics")) hasJavaAdvanced = true;
        }
        
        assertTrue(hasJavaProgramming, "Should include 'Java Programming' note");
        assertTrue(hasJavaAdvanced, "Should include 'Java Advanced Topics' note");
    }
    
    @Test
    void testSearch_TFIDFRanking() {
        // "Java Programming" has "java" twice in content
        // "Java Advanced Topics" has "java" once in content
        // "Java Programming" should rank higher due to higher term frequency
        List<SearchResult> results = searchEngine.search("java", 10);
        
        assertEquals(2, results.size());
        
        // First result should have higher score
        assertTrue(results.get(0).getScore() > 0, "First result should have positive score");
        
        // Results should be sorted by score (descending)
        if (results.size() > 1) {
            assertTrue(results.get(0).getScore() >= results.get(1).getScore(),
                      "Results should be sorted by score in descending order");
        }
    }
    
    @Test
    void testSearch_MultipleTerms() {
        List<SearchResult> results = searchEngine.search("java programming", 10);
        
        assertNotNull(results);
        assertTrue(results.size() >= 1, "Should find notes with 'java' or 'programming'");
        
        // "Java Programming" should rank highest as it has both terms
        assertEquals("Java Programming", results.get(0).getNote().getTitle(),
                    "Note with both terms should rank highest");
    }
    
    @Test
    void testSearch_NoResults() {
        List<SearchResult> results = searchEngine.search("nonexistent", 10);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list for no matches");
    }
    
    @Test
    void testSearch_EmptyQuery() {
        List<SearchResult> results = searchEngine.search("", 10);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list for empty query");
    }
    
    @Test
    void testSearch_NullQuery() {
        List<SearchResult> results = searchEngine.search(null, 10);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list for null query");
    }
    
    @Test
    void testSearch_WhitespaceQuery() {
        List<SearchResult> results = searchEngine.search("   ", 10);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list for whitespace-only query");
    }
    
    @Test
    void testSearch_CaseInsensitive() {
        List<SearchResult> results1 = searchEngine.search("JAVA", 10);
        List<SearchResult> results2 = searchEngine.search("java", 10);
        List<SearchResult> results3 = searchEngine.search("Java", 10);
        
        assertEquals(results1.size(), results2.size(), "Case should not affect results");
        assertEquals(results2.size(), results3.size(), "Case should not affect results");
    }
    
    @Test
    void testSearch_LimitResults() {
        List<SearchResult> results = searchEngine.search("programming", 1);
        
        assertNotNull(results);
        assertEquals(1, results.size(), "Should limit results to specified count");
    }
    
    @Test
    void testSearch_LimitZero() {
        List<SearchResult> results = searchEngine.search("programming", 0);
        
        assertNotNull(results);
        // Should return all results when limit is 0 or negative
        assertTrue(results.size() >= 2, "Should return all matching results");
    }
    
    @Test
    void testSearch_LimitNegative() {
        List<SearchResult> results = searchEngine.search("programming", -1);
        
        assertNotNull(results);
        assertTrue(results.size() >= 2, "Should return all matching results");
    }
    
    @Test
    void testSearch_SpecialCharacters() {
        List<SearchResult> results = searchEngine.search("java!", 10);
        
        // Should tokenize and search for "java" (punctuation removed)
        assertNotNull(results);
        assertEquals(2, results.size(), "Should handle special characters in query");
    }
    
    @Test
    void testSearch_SearchesTitle() {
        List<SearchResult> results = searchEngine.search("advanced", 10);
        
        assertNotNull(results);
        assertEquals(1, results.size(), "Should search in note titles");
        assertEquals("Java Advanced Topics", results.get(0).getNote().getTitle());
    }
    
    @Test
    void testSearch_SearchesContent() {
        List<SearchResult> results = searchEngine.search("concurrency", 10);
        
        assertNotNull(results);
        assertEquals(1, results.size(), "Should search in note content");
        assertEquals("Java Advanced Topics", results.get(0).getNote().getTitle());
    }
    
    @Test
    void testSearch_EmptyNotesList() {
        SearchEngine emptyEngine = new SearchEngine(new ArrayList<>());
        List<SearchResult> results = emptyEngine.search("java", 10);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list for empty notes collection");
    }
    
    @Test
    void testSearch_NullNotesList() {
        SearchEngine nullEngine = new SearchEngine(null);
        List<SearchResult> results = nullEngine.search("java", 10);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should handle null notes collection gracefully");
    }
    
    @Test
    void testSearch_ResultsHaveSnippets() {
        List<SearchResult> results = searchEngine.search("java", 10);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        for (SearchResult result : results) {
            assertNotNull(result.getSnippet(), "Each result should have a snippet");
            assertTrue(result.getSnippet().contains("**"), 
                      "Snippet should contain highlighted terms");
        }
    }
    
    @Test
    void testSearch_ResultsHaveScores() {
        List<SearchResult> results = searchEngine.search("java", 10);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        for (SearchResult result : results) {
            assertTrue(result.getScore() > 0, "Each result should have a positive score");
        }
    }
    
    @Test
    void testSearch_NullNoteInList() {
        notes.add(null);
        SearchEngine engineWithNull = new SearchEngine(notes);
        
        List<SearchResult> results = engineWithNull.search("java", 10);
        
        assertNotNull(results);
        assertEquals(2, results.size(), "Should skip null notes gracefully");
    }
    
    @Test
    void testSearch_NoteWithNullTitle() {
        Note noteWithNullTitle = new Note(5, null, "Content with java keyword",
                                         Arrays.asList("test"), 
                                         LocalDateTime.now(), LocalDateTime.now());
        notes.add(noteWithNullTitle);
        SearchEngine engine = new SearchEngine(notes);
        
        List<SearchResult> results = engine.search("java", 10);
        
        assertNotNull(results);
        assertTrue(results.size() >= 2, "Should handle notes with null titles");
    }
    
    @Test
    void testSearch_NoteWithNullContent() {
        Note noteWithNullContent = new Note(5, "Title with java", null,
                                           Arrays.asList("test"),
                                           LocalDateTime.now(), LocalDateTime.now());
        notes.add(noteWithNullContent);
        SearchEngine engine = new SearchEngine(notes);
        
        List<SearchResult> results = engine.search("java", 10);
        
        assertNotNull(results);
        assertTrue(results.size() >= 2, "Should handle notes with null content");
    }
    
    @Test
    void testSearch_PunctuationInQuery() {
        List<SearchResult> results = searchEngine.search("java, programming!", 10);
        
        assertNotNull(results);
        assertTrue(results.size() >= 1, "Should handle punctuation in query");
    }
    
    @Test
    void testSearch_SingleCharacterQuery() {
        List<SearchResult> results = searchEngine.search("a", 10);
        
        assertNotNull(results);
        // May or may not have results depending on content
    }
    
    @Test
    void testSearch_VeryLongQuery() {
        String longQuery = "java ".repeat(100);
        List<SearchResult> results = searchEngine.search(longQuery, 10);
        
        assertNotNull(results);
        // Should handle long queries without crashing
    }
}
