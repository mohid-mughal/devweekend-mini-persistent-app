package com.knowledgebase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manual test to demonstrate SearchEngine.search functionality with TF-IDF.
 * This can be run manually to verify the implementation works correctly.
 */
public class SearchEngineSearchManualTest {
    
    public static void main(String[] args) {
        // Create test notes
        ArrayList<Note> notes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        Note note1 = new Note(1, "Java Programming Basics", 
                             "Java is a popular programming language. Java is used for enterprise applications. " +
                             "Java provides strong typing and object-oriented features.",
                             Arrays.asList("programming", "java"),
                             now, now);
        
        Note note2 = new Note(2, "Python for Beginners",
                             "Python is a simple programming language. Python is great for beginners. " +
                             "Python has clean syntax and is easy to learn.",
                             Arrays.asList("programming", "python"),
                             now, now);
        
        Note note3 = new Note(3, "Web Development Guide",
                             "Web development involves HTML, CSS, and JavaScript. Modern web apps use frameworks " +
                             "like React and Vue. Web development is a valuable skill.",
                             Arrays.asList("web", "development"),
                             now, now);
        
        Note note4 = new Note(4, "Advanced Java Topics",
                             "Advanced Java includes concurrency, streams, and design patterns. " +
                             "Java 11 introduced new features for modern development.",
                             Arrays.asList("java", "advanced"),
                             now, now);
        
        Note note5 = new Note(5, "Database Design",
                             "Database design is crucial for application performance. SQL and NoSQL databases " +
                             "serve different purposes. Choose the right database for your needs.",
                             Arrays.asList("database", "design"),
                             now, now);
        
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        notes.add(note5);
        
        SearchEngine engine = new SearchEngine(notes);
        
        System.out.println("=== SearchEngine.search Manual Tests ===\n");
        System.out.println("Total notes in collection: " + notes.size() + "\n");
        
        // Test 1: Single term search
        System.out.println("Test 1: Search for 'java'");
        List<SearchResult> results1 = engine.search("java", 10);
        printResults(results1);
        
        // Test 2: Multiple terms search
        System.out.println("\nTest 2: Search for 'java programming'");
        List<SearchResult> results2 = engine.search("java programming", 10);
        printResults(results2);
        
        // Test 3: No results
        System.out.println("\nTest 3: Search for 'nonexistent'");
        List<SearchResult> results3 = engine.search("nonexistent", 10);
        printResults(results3);
        
        // Test 4: Common term (appears in multiple documents)
        System.out.println("\nTest 4: Search for 'programming'");
        List<SearchResult> results4 = engine.search("programming", 10);
        printResults(results4);
        
        // Test 5: Rare term (appears in fewer documents)
        System.out.println("\nTest 5: Search for 'database'");
        List<SearchResult> results5 = engine.search("database", 10);
        printResults(results5);
        
        // Test 6: Search with limit
        System.out.println("\nTest 6: Search for 'java' with limit=1");
        List<SearchResult> results6 = engine.search("java", 1);
        printResults(results6);
        
        // Test 7: Case insensitive search
        System.out.println("\nTest 7: Search for 'JAVA' (case insensitive)");
        List<SearchResult> results7 = engine.search("JAVA", 10);
        printResults(results7);
        
        // Test 8: Search with punctuation
        System.out.println("\nTest 8: Search for 'java, programming!' (with punctuation)");
        List<SearchResult> results8 = engine.search("java, programming!", 10);
        printResults(results8);
        
        // Test 9: Empty query
        System.out.println("\nTest 9: Search for '' (empty query)");
        List<SearchResult> results9 = engine.search("", 10);
        printResults(results9);
        
        // Test 10: Demonstrate TF-IDF ranking
        System.out.println("\nTest 10: TF-IDF Ranking Demonstration");
        System.out.println("Searching for 'java' - note with higher term frequency should rank higher");
        List<SearchResult> results10 = engine.search("java", 10);
        printResults(results10);
        System.out.println("\nNote: 'Java Programming Basics' has 'java' 3 times in content");
        System.out.println("      'Advanced Java Topics' has 'java' 2 times in content");
        System.out.println("      Higher frequency should result in higher TF-IDF score");
        
        System.out.println("\n=== All manual tests completed ===");
    }
    
    private static void printResults(List<SearchResult> results) {
        if (results.isEmpty()) {
            System.out.println("  No results found.");
            return;
        }
        
        System.out.println("  Found " + results.size() + " result(s):");
        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            System.out.println("\n  " + (i + 1) + ". " + result.getNote().getTitle());
            System.out.println("     Score: " + String.format("%.6f", result.getScore()));
            System.out.println("     Snippet: " + result.getSnippet());
        }
    }
}
