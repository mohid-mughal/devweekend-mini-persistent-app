package com.knowledgebase;

import java.util.ArrayList;

/**
 * Manual test to demonstrate SearchEngine.generateSnippet functionality.
 * This can be run manually to verify the implementation works correctly.
 */
public class SearchEngineManualTest {
    
    public static void main(String[] args) {
        ArrayList<Note> notes = new ArrayList<>();
        SearchEngine engine = new SearchEngine(notes);
        
        System.out.println("=== SearchEngine.generateSnippet Manual Tests ===\n");
        
        // Test 1: Basic highlighting
        System.out.println("Test 1: Basic highlighting");
        String content1 = "This is a simple test content with some search terms in it.";
        String[] terms1 = {"search", "terms"};
        String result1 = engine.generateSnippet(content1, terms1);
        System.out.println("Content: " + content1);
        System.out.println("Terms: search, terms");
        System.out.println("Result: " + result1);
        System.out.println();
        
        // Test 2: Case insensitive
        System.out.println("Test 2: Case insensitive matching");
        String content2 = "Java is a programming language. JAVA is widely used.";
        String[] terms2 = {"java"};
        String result2 = engine.generateSnippet(content2, terms2);
        System.out.println("Content: " + content2);
        System.out.println("Terms: java");
        System.out.println("Result: " + result2);
        System.out.println();
        
        // Test 3: Term at start
        System.out.println("Test 3: Term at start of content");
        String content3 = "Search term at the beginning of the content and more text follows.";
        String[] terms3 = {"search"};
        String result3 = engine.generateSnippet(content3, terms3);
        System.out.println("Content: " + content3);
        System.out.println("Terms: search");
        System.out.println("Result: " + result3);
        System.out.println();
        
        // Test 4: Long content
        System.out.println("Test 4: Long content with term in middle");
        String content4 = "This is a very long piece of content that goes on and on with many words. " +
                         "It contains a search term somewhere in the middle of all this text. " +
                         "The snippet should extract approximately 150 characters around the term. " +
                         "There is more content after the search term as well.";
        String[] terms4 = {"search"};
        String result4 = engine.generateSnippet(content4, terms4);
        System.out.println("Content: " + content4);
        System.out.println("Terms: search");
        System.out.println("Result: " + result4);
        System.out.println("Result length: " + result4.length());
        System.out.println();
        
        // Test 5: No matches
        System.out.println("Test 5: No matches found");
        String content5 = "This is some content without the query words.";
        String[] terms5 = {"missing", "absent"};
        String result5 = engine.generateSnippet(content5, terms5);
        System.out.println("Content: " + content5);
        System.out.println("Terms: missing, absent");
        System.out.println("Result: " + result5);
        System.out.println();
        
        // Test 6: Multiple terms
        System.out.println("Test 6: Multiple terms in snippet");
        String content6 = "This content has multiple search terms including java and programming.";
        String[] terms6 = {"java", "programming", "search"};
        String result6 = engine.generateSnippet(content6, terms6);
        System.out.println("Content: " + content6);
        System.out.println("Terms: java, programming, search");
        System.out.println("Result: " + result6);
        System.out.println();
        
        // Test 7: Empty content
        System.out.println("Test 7: Empty content");
        String content7 = "";
        String[] terms7 = {"search"};
        String result7 = engine.generateSnippet(content7, terms7);
        System.out.println("Content: (empty)");
        System.out.println("Terms: search");
        System.out.println("Result: '" + result7 + "'");
        System.out.println();
        
        // Test 8: Short content
        System.out.println("Test 8: Short content (less than 150 chars)");
        String content8 = "Short text with search term.";
        String[] terms8 = {"search"};
        String result8 = engine.generateSnippet(content8, terms8);
        System.out.println("Content: " + content8);
        System.out.println("Terms: search");
        System.out.println("Result: " + result8);
        System.out.println();
        
        System.out.println("=== All manual tests completed ===");
    }
}
