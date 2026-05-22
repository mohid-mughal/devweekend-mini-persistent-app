package com.knowledgebase;

import java.util.Objects;

/**
 * Represents a search result with a note, relevance score, and snippet.
 * Implements Comparable for sorting by score in descending order.
 */
public class SearchResult implements Comparable<SearchResult> {
    
    private final Note note;
    private final double score;
    private final String snippet;
    
    /**
     * Creates a new SearchResult.
     * 
     * @param note the note that matched the search
     * @param score the relevance score (higher is more relevant)
     * @param snippet a snippet of the note content with highlighted search terms
     */
    public SearchResult(Note note, double score, String snippet) {
        this.note = note;
        this.score = score;
        this.snippet = snippet;
    }
    
    public Note getNote() {
        return note;
    }
    
    public double getScore() {
        return score;
    }
    
    public String getSnippet() {
        return snippet;
    }
    
    @Override
    public int compareTo(SearchResult other) {
        // Sort by score in descending order (higher scores first)
        return Double.compare(other.score, this.score);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return Double.compare(that.score, score) == 0 &&
               Objects.equals(note, that.note) &&
               Objects.equals(snippet, that.snippet);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(note, score, snippet);
    }
    
    @Override
    public String toString() {
        return "SearchResult{" +
               "note=" + (note != null ? note.getTitle() : "null") +
               ", score=" + String.format("%.4f", score) +
               ", snippet='" + snippet + '\'' +
               '}';
    }
}
