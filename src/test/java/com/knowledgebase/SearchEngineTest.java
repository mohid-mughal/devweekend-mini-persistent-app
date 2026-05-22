package com.knowledgebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchEngine class.
 * Tests TF-IDF search functionality, tokenization, and snippet generation.
 */
class SearchEngineTest {
    
    private List<Note> notes;
    private SearchEngine searchEngine;
    
    @BeforeEach
    void setUp() {
        notes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Create test notes
        notes.add(new Note(1, "Java Programming", 
            "Java is a popular programming language used for building applications.", 
            Arrays.asList("programming", "java"), now, now));
        
        notes.add(new Note(2, "Python Basics", 
            "Python is a versatile programming language known for its simplicity.", 
            Arrays.asList("programming", "python"), now, now));
        
        notes.add(new Note(3, "Web Development", 
            "Web development involves creating websites using HTML, CSS, and JavaScript.", 
            Arrays.asList("web", "development"), now, now));
        
        notes.add(new Note(4, "Java Advanced Topics", 
            "Advanced Java programming includes concurrency, streams, and design patterns.", 
            Arrays.asList("programming", "java", "advanced"), now, now));
        
        searchEngine = new SearchEngine(notes);
    }
    
    @Test
    void testSearchReturnsRelevantResults() {
        List<SearchResult> results = searchEngine.search("Java programming", 10);
        
        assertFalse(results.isEmpty(), "Search should return results");
        assertTrue(results.size() <= 10, "Results should respect limit");
        
        // First result should be most relevant (contains both terms)
        assertEquals("Java Programming", results.get(0).getNote().getTitle());
    }
    
    @Test
    void testSearchRanksByRelevance() {
        List<SearchResult> results = searchEngine.search("Java", 10);
        
        assertFalse(results.isEmpty(), "Search should return results");
        
        // Verify results are sorted by score (descending)
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getScore() >= results.get(i + 1).getScore(),
                "Results should be sorted by score in descending order");
        }
    }
    
    @Test
    void testSearchWithEmptyQuery() {
        List<SearchResult> results = searchEngine.search("", 10);
        assertTrue(results.isEmpty(), "Empty query should return no results");
        
        results = searchEngine.search(null, 10);
        assertTrue(results.isEmpty(), "Null query should return no results");
        
        results = searchEngine.search("   ", 10);
        assertTrue(results.isEmpty(), "Whitespace-only query should return no results");
    }
    
    @Test
    void testSearchWithNoMatches() {
        List<SearchResult> results = searchEngine.search("quantum physics", 10);
        assertTrue(results.isEmpty(), "Search with no matches should return empty list");
    }
    
    @Test
    void testSearchRespectsLimit() {
        List<SearchResult> results = searchEngine.search("programming", 2);
        
        assertEquals(2, results.size(), "Search should respect limit parameter");
    }
    
    @Test
    void testSearchWithZeroLimit() {
        List<SearchResult> results = searchEngine.search("programming", 0);
        
        // Should return all matching results when limit is 0
        assertTrue(results.size() > 0, "Zero limit should return all results");
    }
    
    @Test
    void testTokenize() {
        SearchEngine engine = new SearchEngine(new ArrayList<>());
        
        String[] tokens = engine.tokenize("Hello, World! This is a test.");
        assertArrayEquals(new String[]{"hello", "world", "this", "is", "a", "test"}, tokens);
        
        tokens = engine.tokenize("Java-Programming");
        assertArrayEquals(new String[]{"java", "programming"}, tokens);
        
        tokens = engine.tokenize("   multiple   spaces   ");
        assertArrayEquals(new String[]{"multiple", "spaces"}, tokens);
        
        tokens = engine.tokenize("");
        assertEquals(0, tokens.length, "Empty string should return empty array");
        
        tokens = engine.tokenize(null);
        assertEquals(0, tokens.length, "Null should return empty array");
    }
    
    @Test
    void testSearchCaseInsensitive() {
        List<SearchResult> results1 = searchEngine.search("JAVA", 10);
        List<SearchResult> results2 = searchEngine.search("java", 10);
        List<SearchResult> results3 = searchEngine.search("Java", 10);
        
        assertEquals(results1.size(), results2.size(), "Search should be case-insensitive");
        assertEquals(results2.size(), results3.size(), "Search should be case-insensitive");
    }
    
    @Test
    void testSearchWithPunctuation() {
        List<SearchResult> results = searchEngine.search("Java, programming!", 10);
        
        assertFalse(results.isEmpty(), "Search should handle punctuation in query");
    }
    
    @Test
    void testSearchEmptyNotesList() {
        SearchEngine emptyEngine = new SearchEngine(new ArrayList<>());
        List<SearchResult> results = emptyEngine.search("test", 10);
        
        assertTrue(results.isEmpty(), "Search on empty notes list should return empty results");
    }
    
    @Test
    void testSearchNullNotesList() {
        SearchEngine nullEngine = new SearchEngine(null);
        List<SearchResult> results = nullEngine.search("test", 10);
        
        assertTrue(results.isEmpty(), "Search with null notes list should return empty results");
    }
    
    @Test
    void testSnippetGeneration() {
        List<SearchResult> results = searchEngine.search("Java", 10);
        
        assertFalse(results.isEmpty(), "Should have results");
        
        for (SearchResult result : results) {
            assertNotNull(result.getSnippet(), "Snippet should not be null");
            assertFalse(result.getSnippet().isEmpty(), "Snippet should not be empty");
        }
    }
    
    @Test
    void testTFIDFScoring() {
        // Note with term appearing multiple times should score higher
        List<SearchResult> results = searchEngine.search("programming", 10);
        
        assertFalse(results.isEmpty(), "Should have results");
        
        // All results should have positive scores
        for (SearchResult result : results) {
            assertTrue(result.getScore() > 0, "Score should be positive");
        }
    }
    
    @Test
    void testSearchInTitleAndContent() {
        // Search for term that appears in title
        List<SearchResult> results = searchEngine.search("Web", 10);
        
        assertFalse(results.isEmpty(), "Should find notes with term in title");
        assertEquals("Web Development", results.get(0).getNote().getTitle());
    }
    
    @Test
    void testMultipleQueryTerms() {
        List<SearchResult> results = searchEngine.search("Java programming language", 10);
        
        assertFalse(results.isEmpty(), "Should handle multiple query terms");
        
        // Notes matching more terms should rank higher
        assertTrue(results.get(0).getScore() > 0, "Top result should have positive score");
    }
}
