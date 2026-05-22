package com.knowledgebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for links.json file.
 * Contains the list of all bidirectional links between notes.
 * 
 * Requirements: 7.1, 7.5
 */
public class LinksData {
    
    private List<Link> links;
    
    /**
     * Creates a new LinksData with an empty list.
     */
    public LinksData() {
        this.links = new ArrayList<>();
    }
    
    /**
     * Creates a new LinksData with the specified links.
     * 
     * @param links the list of links
     */
    public LinksData(List<Link> links) {
        this.links = links != null ? new ArrayList<>(links) : new ArrayList<>();
    }
    
    // Getters
    
    public List<Link> getLinks() {
        return links;
    }
    
    // Setters
    
    public void setLinks(List<Link> links) {
        this.links = links != null ? new ArrayList<>(links) : new ArrayList<>();
    }
}
