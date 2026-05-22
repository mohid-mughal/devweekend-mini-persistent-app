package com.knowledgebase;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Provides full-text search functionality using TF-IDF (Term Frequency-Inverse Document Frequency) algorithm.
 * Searches across note titles and content, ranks results by relevance, and generates snippets.
 */
public class SearchEngine {
    
    private final List<Note> notes;
    
    /**
     * Creates a new SearchEngine operating on the provided notes.
     * 
     * @param notes the list of notes to search
     */
    public SearchEngine(List<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
    }
    
    /**
     * Searches notes for the given query and returns ranked results.
     * 
     * @param query the search query
     * @param limit maximum number of results to return
     * @return list of search results sorted by relevance (highest score first)
     */
    public List<SearchResult> search(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        if (notes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Tokenize the query
        String[] queryTerms = tokenize(query);
        
        if (queryTerms.length == 0) {
            return new ArrayList<>();
        }
        
        // Calculate IDF for each query term across all notes
        Map<String, Double> idfScores = calculateIDF(queryTerms);
        
        // Score each note and create search results
        List<SearchResult> results = new ArrayList<>();
        
        for (Note note : notes) {
            // Combine title and content for searching
            String searchableText = note.getTitle() + " " + note.getContent();
            
            // Calculate TF-IDF score for this note
            double score = calculateTFIDF(searchableText, queryTerms, idfScores);
            
            // Only include notes with non-zero scores
            if (score > 0) {
                String snippet = generateSnippet(note.getContent(), queryTerms);
                results.add(new SearchResult(note, score, snippet));
            }
        }
        
        // Sort by score in descending order
        Collections.sort(results);
        
        // Limit results
        if (limit > 0 && results.size() > limit) {
            return results.subList(0, limit);
        }
        
        return results;
    }
    
    /**
     * Tokenizes text into words (lowercase, punctuation removed).
     * 
     * @param text the text to tokenize
     * @return array of tokens
     */
    String[] tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        
        // Convert to lowercase and remove punctuation
        // Keep only alphanumeric characters and spaces
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        
        // Split on whitespace and filter out empty strings
        return Arrays.stream(cleaned.split("\\s+"))
                .filter(token -> !token.isEmpty())
                .toArray(String[]::new);
    }
    
    /**
     * Calculates Inverse Document Frequency (IDF) for each query term.
     * IDF = log(total documents / documents containing term)
     * 
     * @param queryTerms the terms to calculate IDF for
     * @return map of term to IDF score
     */
    private Map<String, Double> calculateIDF(String[] queryTerms) {
        Map<String, Double> idfScores = new HashMap<>();
        int totalDocuments = notes.size();
        
        for (String term : queryTerms) {
            // Count how many documents contain this term
            int documentsWithTerm = 0;
            
            for (Note note : notes) {
                String searchableText = (note.getTitle() + " " + note.getContent()).toLowerCase();
                if (searchableText.contains(term)) {
                    documentsWithTerm++;
                }
            }
            
            // Calculate IDF: log(N / df)
            // Add 1 to avoid division by zero
            double idf = Math.log((double) totalDocuments / (documentsWithTerm + 1));
            idfScores.put(term, idf);
        }
        
        return idfScores;
    }
    
    /**
     * Calculates TF-IDF score for a text given query terms and IDF scores.
     * TF-IDF = sum of (term frequency * inverse document frequency) for each query term
     * 
     * @param text the text to score
     * @param queryTerms the query terms
     * @param idfScores pre-calculated IDF scores for query terms
     * @return TF-IDF score
     */
    private double calculateTFIDF(String text, String[] queryTerms, Map<String, Double> idfScores) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        
        // Tokenize the text
        String[] tokens = tokenize(text);
        
        if (tokens.length == 0) {
            return 0.0;
        }
        
        // Count term frequencies
        Map<String, Integer> termFrequencies = new HashMap<>();
        for (String token : tokens) {
            termFrequencies.put(token, termFrequencies.getOrDefault(token, 0) + 1);
        }
        
        // Calculate TF-IDF score
        double score = 0.0;
        
        for (String term : queryTerms) {
            if (termFrequencies.containsKey(term)) {
                // TF = term count / total tokens
                double tf = (double) termFrequencies.get(term) / tokens.length;
                
                // IDF from pre-calculated scores
                double idf = idfScores.getOrDefault(term, 0.0);
                
                // TF-IDF = TF * IDF
                score += tf * idf;
            }
        }
        
        return score;
    }
    
    /**
     * Generates a snippet from content with search terms highlighted.
     * Finds the first occurrence of any query term and extracts surrounding context.
     * 
     * @param content the note content
     * @param queryTerms the search terms
     * @return snippet with highlighted terms (using **term** markers)
     */
    private String generateSnippet(String content, String[] queryTerms) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        if (queryTerms == null || queryTerms.length == 0) {
            // Return first 150 characters if no query terms
            return content.length() <= 150 ? content : content.substring(0, 150) + "...";
        }
        
        String contentLower = content.toLowerCase();
        
        // Find the first occurrence of any query term
        int firstOccurrence = -1;
        String matchedTerm = null;
        
        for (String term : queryTerms) {
            int index = contentLower.indexOf(term);
            if (index != -1 && (firstOccurrence == -1 || index < firstOccurrence)) {
                firstOccurrence = index;
                matchedTerm = term;
            }
        }
        
        // If no term found, return beginning of content
        if (firstOccurrence == -1) {
            return content.length() <= 150 ? content : content.substring(0, 150) + "...";
        }
        
        // Extract context around the matched term (150 characters total)
        int snippetLength = 150;
        int contextBefore = 50;
        
        int start = Math.max(0, firstOccurrence - contextBefore);
        int end = Math.min(content.length(), start + snippetLength);
        
        // Adjust start if we're at the end of content
        if (end - start < snippetLength && start > 0) {
            start = Math.max(0, end - snippetLength);
        }
        
        String snippet = content.substring(start, end);
        
        // Add ellipsis if we're not at the boundaries
        if (start > 0) {
            snippet = "..." + snippet;
        }
        if (end < content.length()) {
            snippet = snippet + "...";
        }
        
        // Highlight all query terms in the snippet (case-insensitive)
        for (String term : queryTerms) {
            // Use regex with case-insensitive flag to replace all occurrences
            snippet = snippet.replaceAll("(?i)" + Pattern.quote(term), "**$0**");
        }
        
        return snippet;
    }
}
