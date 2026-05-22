package com.knowledgebase;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Link class.
 */
class LinkTest {
    
    @Test
    void testLinkConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Link link = new Link(1, "Target Note", 2, now);
        
        assertEquals(1, link.getFromNoteId());
        assertEquals("Target Note", link.getToNoteTitle());
        assertEquals(2, link.getToNoteId());
        assertEquals(now, link.getCreatedAt());
    }
    
    @Test
    void testLinkConstructor_NullToNoteId() {
        LocalDateTime now = LocalDateTime.now();
        Link link = new Link(1, "Orphaned Target", null, now);
        
        assertEquals(1, link.getFromNoteId());
        assertEquals("Orphaned Target", link.getToNoteTitle());
        assertNull(link.getToNoteId());
        assertEquals(now, link.getCreatedAt());
    }
    
    @Test
    void testLinkSetters() {
        Link link = new Link();
        LocalDateTime now = LocalDateTime.now();
        
        link.setFromNoteId(5);
        link.setToNoteTitle("New Target");
        link.setToNoteId(10);
        link.setCreatedAt(now);
        
        assertEquals(5, link.getFromNoteId());
        assertEquals("New Target", link.getToNoteTitle());
        assertEquals(10, link.getToNoteId());
        assertEquals(now, link.getCreatedAt());
    }
    
    @Test
    void testLinkEquals_SameValues() {
        LocalDateTime now = LocalDateTime.now();
        Link link1 = new Link(1, "Target", 2, now);
        Link link2 = new Link(1, "Target", 2, now);
        
        assertEquals(link1, link2);
        assertEquals(link1.hashCode(), link2.hashCode());
    }
    
    @Test
    void testLinkEquals_DifferentValues() {
        LocalDateTime now = LocalDateTime.now();
        Link link1 = new Link(1, "Target", 2, now);
        Link link2 = new Link(1, "Different", 2, now);
        
        assertNotEquals(link1, link2);
    }
    
    @Test
    void testLinkEquals_SameObject() {
        LocalDateTime now = LocalDateTime.now();
        Link link = new Link(1, "Target", 2, now);
        
        assertEquals(link, link);
    }
    
    @Test
    void testLinkEquals_Null() {
        LocalDateTime now = LocalDateTime.now();
        Link link = new Link(1, "Target", 2, now);
        
        assertNotEquals(link, null);
    }
    
    @Test
    void testLinkToString() {
        LocalDateTime now = LocalDateTime.now();
        Link link = new Link(1, "Target Note", 2, now);
        
        String str = link.toString();
        
        assertTrue(str.contains("fromNoteId=1"));
        assertTrue(str.contains("toNoteTitle='Target Note'"));
        assertTrue(str.contains("toNoteId=2"));
    }
    
    @Test
    void testLinkToString_NullToNoteId() {
        LocalDateTime now = LocalDateTime.now();
        Link link = new Link(1, "Orphaned", null, now);
        
        String str = link.toString();
        
        assertTrue(str.contains("fromNoteId=1"));
        assertTrue(str.contains("toNoteTitle='Orphaned'"));
        assertTrue(str.contains("toNoteId=null"));
    }
}
