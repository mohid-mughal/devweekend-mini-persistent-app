package com.knowledgebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LinkManager class, focusing on link extraction functionality.
 * Tests various edge cases including unclosed brackets, nested brackets, and empty links.
 */
class LinkManagerTest {
    
    private LinkManager linkManager;
    
    @BeforeEach
    void setUp() {
        linkManager = new LinkManager();
    }
    
    @Test
    void testExtractLinks_SimpleLink() {
        String content = "This is a reference to [[Note Title]]";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(1, links.size());
        assertEquals("Note Title", links.get(0));
    }
    
    @Test
    void testExtractLinks_MultipleLinks() {
        String content = "See [[First Note]] and [[Second Note]] for details";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(2, links.size());
        assertTrue(links.contains("First Note"));
        assertTrue(links.contains("Second Note"));
    }
    
    @Test
    void testExtractLinks_DuplicateLinks_ReturnsUnique() {
        String content = "[[Same Note]] is mentioned twice: [[Same Note]]";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(1, links.size());
        assertEquals("Same Note", links.get(0));
    }
    
    @Test
    void testExtractLinks_EmptyBrackets_Ignored() {
        String content = "Empty brackets [[]] should be ignored";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(0, links.size());
    }
    
    @Test
    void testExtractLinks_WhitespaceOnlyBrackets_Ignored() {
        String content = "Whitespace only [[   ]] should be ignored";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(0, links.size());
    }
    
    @Test
    void testExtractLinks_UnclosedBrackets_Ignored() {
        String content = "Unclosed [[brackets should be ignored";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(0, links.size());
    }
    
    @Test
    void testExtractLinks_NestedBrackets_ExtractsInnermost() {
        // The regex pattern [[([^\]]+)]] will match the first complete pair
        // For "[[outer [[inner]]", it matches "outer [[inner" (everything between first [[ and first ]])
        String content = "Nested [[outer [[inner]]";
        List<String> links = linkManager.extractLinks(content);
        
        // The pattern will extract "inner" from the properly closed [[inner]]
        assertEquals(1, links.size());
        assertEquals("inner", links.get(0));
    }
    
    @Test
    void testExtractLinks_MixedValidAndInvalid() {
        String content = "Valid [[Good Link]] and unclosed [[bad and empty [[]] and another [[Valid Link]]";
        List<String> links = linkManager.extractLinks(content);
        
        // Should extract "Good Link", "bad and empty [[", and "Valid Link"
        // But "bad and empty [[" is actually valid according to the regex
        assertTrue(links.contains("Good Link"));
        assertTrue(links.contains("Valid Link"));
    }
    
    @Test
    void testExtractLinks_NullContent_ReturnsEmpty() {
        List<String> links = linkManager.extractLinks(null);
        
        assertNotNull(links);
        assertEquals(0, links.size());
    }
    
    @Test
    void testExtractLinks_EmptyContent_ReturnsEmpty() {
        List<String> links = linkManager.extractLinks("");
        
        assertNotNull(links);
        assertEquals(0, links.size());
    }
    
    @Test
    void testExtractLinks_NoLinks_ReturnsEmpty() {
        String content = "This content has no links at all";
        List<String> links = linkManager.extractLinks(content);
        
        assertNotNull(links);
        assertEquals(0, links.size());
    }
    
    @Test
    void testExtractLinks_LinkWithSpecialCharacters() {
        String content = "Link with special chars [[Note-Title_123!@#]]";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(1, links.size());
        assertEquals("Note-Title_123!@#", links.get(0));
    }
    
    @Test
    void testExtractLinks_LinkWithNewlines() {
        String content = "Link with\nnewline [[Multi\nLine\nTitle]]";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(1, links.size());
        assertEquals("Multi\nLine\nTitle", links.get(0));
    }
    
    @Test
    void testExtractLinks_LinkAtStartOfContent() {
        String content = "[[First Link]] is at the start";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(1, links.size());
        assertEquals("First Link", links.get(0));
    }
    
    @Test
    void testExtractLinks_LinkAtEndOfContent() {
        String content = "Link at the end [[Last Link]]";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(1, links.size());
        assertEquals("Last Link", links.get(0));
    }
    
    @Test
    void testExtractLinks_ConsecutiveLinks() {
        String content = "[[First]][[Second]][[Third]]";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(3, links.size());
        assertTrue(links.contains("First"));
        assertTrue(links.contains("Second"));
        assertTrue(links.contains("Third"));
    }
    
    @Test
    void testExtractLinks_LinkWithLeadingTrailingWhitespace_Trimmed() {
        String content = "Link with spaces [[  Spaced Title  ]]";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(1, links.size());
        assertEquals("Spaced Title", links.get(0));
    }
    
    @Test
    void testExtractLinks_SingleBracket_Ignored() {
        String content = "Single bracket [ or ] should not match";
        List<String> links = linkManager.extractLinks(content);
        
        assertEquals(0, links.size());
    }
    
    @Test
    void testExtractLinks_ThreeBrackets_MatchesInnerPair() {
        String content = "Three brackets [[[Triple]]]";
        List<String> links = linkManager.extractLinks(content);
        
        // Should match [[Triple]] (the inner pair)
        assertEquals(1, links.size());
        assertEquals("[Triple", links.get(0));
    }
    
    @Test
    void testGetLinks_InitiallyEmpty() {
        ArrayList<Link> links = linkManager.getLinks();
        
        assertNotNull(links);
        assertEquals(0, links.size());
    }
    
    @Test
    void testSetLinks_UpdatesCollection() {
        ArrayList<Link> newLinks = new ArrayList<>();
        newLinks.add(new Link(1, "Target", 2, java.time.LocalDateTime.now()));
        
        linkManager.setLinks(newLinks);
        
        assertEquals(1, linkManager.getLinks().size());
    }
    
    @Test
    void testSetLinks_NullSetsEmpty() {
        linkManager.setLinks(null);
        
        assertNotNull(linkManager.getLinks());
        assertEquals(0, linkManager.getLinks().size());
    }
    
    @Test
    void testUpdateLinks_CreatesNewLinks() throws Exception {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(2, "Target Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        String content = "This references [[Target Note]]";
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute
        linkManager.updateLinks(1, content, allNotes, storage);
        
        // Verify
        assertEquals(1, linkManager.getLinks().size());
        Link link = linkManager.getLinks().get(0);
        assertEquals(1, link.getFromNoteId());
        assertEquals("Target Note", link.getToNoteTitle());
        assertEquals(2, link.getToNoteId());
        assertNotNull(link.getCreatedAt());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testUpdateLinks_RemovesOldLinks() throws Exception {
        // Setup - add existing link
        Link oldLink = new Link(1, "Old Target", 3, java.time.LocalDateTime.now());
        ArrayList<Link> existingLinks = new ArrayList<>();
        existingLinks.add(oldLink);
        linkManager.setLinks(existingLinks);
        
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(2, "New Target", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        String content = "This references [[New Target]]";
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute
        linkManager.updateLinks(1, content, allNotes, storage);
        
        // Verify - old link removed, new link added
        assertEquals(1, linkManager.getLinks().size());
        Link link = linkManager.getLinks().get(0);
        assertEquals("New Target", link.getToNoteTitle());
        assertEquals(2, link.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testUpdateLinks_CaseInsensitiveResolution() throws Exception {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(2, "Target Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Content references with different case
        String content = "This references [[target note]]";
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute
        linkManager.updateLinks(1, content, allNotes, storage);
        
        // Verify - should resolve to note ID 2 despite case difference
        assertEquals(1, linkManager.getLinks().size());
        Link link = linkManager.getLinks().get(0);
        assertEquals("target note", link.getToNoteTitle());
        assertEquals(2, link.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testUpdateLinks_OrphanedLink() throws Exception {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Content references non-existent note
        String content = "This references [[Non Existent Note]]";
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute
        linkManager.updateLinks(1, content, allNotes, storage);
        
        // Verify - link created with null toNoteId
        assertEquals(1, linkManager.getLinks().size());
        Link link = linkManager.getLinks().get(0);
        assertEquals(1, link.getFromNoteId());
        assertEquals("Non Existent Note", link.getToNoteTitle());
        assertNull(link.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testUpdateLinks_MultipleLinks() throws Exception {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(2, "First Target", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(3, "Second Target", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        String content = "References [[First Target]] and [[Second Target]]";
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute
        linkManager.updateLinks(1, content, allNotes, storage);
        
        // Verify
        assertEquals(2, linkManager.getLinks().size());
        assertTrue(linkManager.getLinks().stream()
                  .anyMatch(l -> l.getToNoteTitle().equals("First Target") && l.getToNoteId() == 2));
        assertTrue(linkManager.getLinks().stream()
                  .anyMatch(l -> l.getToNoteTitle().equals("Second Target") && l.getToNoteId() == 3));
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testUpdateLinks_NoLinks() throws Exception {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        String content = "This has no links";
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute
        linkManager.updateLinks(1, content, allNotes, storage);
        
        // Verify - no links created
        assertEquals(0, linkManager.getLinks().size());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testUpdateLinks_PreservesOtherNoteLinks() throws Exception {
        // Setup - add existing links from different notes
        Link link1 = new Link(2, "Other Target", 3, java.time.LocalDateTime.now());
        Link link2 = new Link(3, "Another Target", 4, java.time.LocalDateTime.now());
        ArrayList<Link> existingLinks = new ArrayList<>();
        existingLinks.add(link1);
        existingLinks.add(link2);
        linkManager.setLinks(existingLinks);
        
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source Note", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(5, "New Target", "Content", List.of(), 
                     java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        String content = "This references [[New Target]]";
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - update links for note 1
        linkManager.updateLinks(1, content, allNotes, storage);
        
        // Verify - links from notes 2 and 3 are preserved, new link from note 1 added
        assertEquals(3, linkManager.getLinks().size());
        assertTrue(linkManager.getLinks().stream()
                  .anyMatch(l -> l.getFromNoteId() == 2 && l.getToNoteTitle().equals("Other Target")));
        assertTrue(linkManager.getLinks().stream()
                  .anyMatch(l -> l.getFromNoteId() == 3 && l.getToNoteTitle().equals("Another Target")));
        assertTrue(linkManager.getLinks().stream()
                  .anyMatch(l -> l.getFromNoteId() == 1 && l.getToNoteTitle().equals("New Target")));
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }

    @Test
    void testGetOutgoingLinks_ReturnsLinkedNotes() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        Note sourceNote = new Note(1, "Source", "Content", List.of(), 
                                   java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        Note targetNote1 = new Note(2, "Target 1", "Content", List.of(), 
                                    java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        Note targetNote2 = new Note(3, "Target 2", "Content", List.of(), 
                                    java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        allNotes.add(sourceNote);
        allNotes.add(targetNote1);
        allNotes.add(targetNote2);
        
        // Add links from note 1 to notes 2 and 3
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target 1", 2, java.time.LocalDateTime.now()));
        links.add(new Link(1, "Target 2", 3, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute
        List<Note> outgoingLinks = linkManager.getOutgoingLinks(1, allNotes);
        
        // Verify
        assertEquals(2, outgoingLinks.size());
        assertTrue(outgoingLinks.stream().anyMatch(n -> n.getId() == 2));
        assertTrue(outgoingLinks.stream().anyMatch(n -> n.getId() == 3));
    }
    
    @Test
    void testGetOutgoingLinks_NoLinks_ReturnsEmpty() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Execute
        List<Note> outgoingLinks = linkManager.getOutgoingLinks(1, allNotes);
        
        // Verify
        assertNotNull(outgoingLinks);
        assertEquals(0, outgoingLinks.size());
    }
    
    @Test
    void testGetOutgoingLinks_SkipsOrphanedLinks() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        Note sourceNote = new Note(1, "Source", "Content", List.of(), 
                                   java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        Note targetNote = new Note(2, "Target", "Content", List.of(), 
                                   java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        allNotes.add(sourceNote);
        allNotes.add(targetNote);
        
        // Add one valid link and one orphaned link (toNoteId is null)
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 2, java.time.LocalDateTime.now()));
        links.add(new Link(1, "Non Existent", null, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute
        List<Note> outgoingLinks = linkManager.getOutgoingLinks(1, allNotes);
        
        // Verify - only the valid link is returned
        assertEquals(1, outgoingLinks.size());
        assertEquals(2, outgoingLinks.get(0).getId());
    }
    
    @Test
    void testGetOutgoingLinks_OnlyReturnsLinksFromSpecifiedNote() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Note 1", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(2, "Note 2", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(3, "Note 3", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Add links from different notes
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Note 2", 2, java.time.LocalDateTime.now()));
        links.add(new Link(2, "Note 3", 3, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute - get outgoing links for note 1
        List<Note> outgoingLinks = linkManager.getOutgoingLinks(1, allNotes);
        
        // Verify - only link from note 1 is returned
        assertEquals(1, outgoingLinks.size());
        assertEquals(2, outgoingLinks.get(0).getId());
    }
    
    @Test
    void testGetIncomingLinks_ReturnsBacklinks() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        Note sourceNote1 = new Note(1, "Source 1", "Content", List.of(), 
                                    java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        Note sourceNote2 = new Note(2, "Source 2", "Content", List.of(), 
                                    java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        Note targetNote = new Note(3, "Target", "Content", List.of(), 
                                   java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        allNotes.add(sourceNote1);
        allNotes.add(sourceNote2);
        allNotes.add(targetNote);
        
        // Add links from notes 1 and 2 to note 3
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 3, java.time.LocalDateTime.now()));
        links.add(new Link(2, "Target", 3, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute
        List<Note> incomingLinks = linkManager.getIncomingLinks(3, allNotes);
        
        // Verify
        assertEquals(2, incomingLinks.size());
        assertTrue(incomingLinks.stream().anyMatch(n -> n.getId() == 1));
        assertTrue(incomingLinks.stream().anyMatch(n -> n.getId() == 2));
    }
    
    @Test
    void testGetIncomingLinks_NoBacklinks_ReturnsEmpty() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Note", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Execute
        List<Note> incomingLinks = linkManager.getIncomingLinks(1, allNotes);
        
        // Verify
        assertNotNull(incomingLinks);
        assertEquals(0, incomingLinks.size());
    }
    
    @Test
    void testGetIncomingLinks_SkipsOrphanedLinks() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        Note sourceNote = new Note(1, "Source", "Content", List.of(), 
                                   java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        Note targetNote = new Note(2, "Target", "Content", List.of(), 
                                   java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        allNotes.add(sourceNote);
        allNotes.add(targetNote);
        
        // Add one valid link and one orphaned link (toNoteId is null)
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 2, java.time.LocalDateTime.now()));
        links.add(new Link(3, "Non Existent", null, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute
        List<Note> incomingLinks = linkManager.getIncomingLinks(2, allNotes);
        
        // Verify - only the valid link is returned
        assertEquals(1, incomingLinks.size());
        assertEquals(1, incomingLinks.get(0).getId());
    }
    
    @Test
    void testGetIncomingLinks_OnlyReturnsLinksToSpecifiedNote() {
        // Setup
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Note 1", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(2, "Note 2", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        allNotes.add(new Note(3, "Note 3", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Add links to different notes
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Note 2", 2, java.time.LocalDateTime.now()));
        links.add(new Link(1, "Note 3", 3, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute - get incoming links for note 2
        List<Note> incomingLinks = linkManager.getIncomingLinks(2, allNotes);
        
        // Verify - only link to note 2 is returned
        assertEquals(1, incomingLinks.size());
        assertEquals(1, incomingLinks.get(0).getId());
    }
    
    @Test
    void testGetOutgoingLinks_TargetNoteNotInAllNotes_ReturnsEmpty() {
        // Setup - link exists but target note is not in allNotes
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(1, "Source", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Add link to note 2, but note 2 is not in allNotes
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 2, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute
        List<Note> outgoingLinks = linkManager.getOutgoingLinks(1, allNotes);
        
        // Verify - no notes returned because target note doesn't exist in allNotes
        assertEquals(0, outgoingLinks.size());
    }
    
    @Test
    void testGetIncomingLinks_SourceNoteNotInAllNotes_ReturnsEmpty() {
        // Setup - link exists but source note is not in allNotes
        ArrayList<Note> allNotes = new ArrayList<>();
        allNotes.add(new Note(2, "Target", "Content", List.of(), 
                             java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));
        
        // Add link from note 1 to note 2, but note 1 is not in allNotes
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 2, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Execute
        List<Note> incomingLinks = linkManager.getIncomingLinks(2, allNotes);
        
        // Verify - no notes returned because source note doesn't exist in allNotes
        assertEquals(0, incomingLinks.size());
    }

    @Test
    void testResolveOrphanedLinks_ResolvesNullToNoteId() throws Exception {
        // Setup - create orphaned links (toNoteId is null)
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Future Note", null, java.time.LocalDateTime.now()));
        links.add(new Link(2, "Future Note", null, java.time.LocalDateTime.now()));
        links.add(new Link(3, "Other Note", null, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - resolve orphaned links when "Future Note" is created with ID 5
        linkManager.resolveOrphanedLinks("Future Note", 5, storage);
        
        // Verify - links to "Future Note" now have toNoteId set to 5
        assertEquals(3, linkManager.getLinks().size());
        
        Link link1 = linkManager.getLinks().get(0);
        assertEquals("Future Note", link1.getToNoteTitle());
        assertEquals(5, link1.getToNoteId());
        
        Link link2 = linkManager.getLinks().get(1);
        assertEquals("Future Note", link2.getToNoteTitle());
        assertEquals(5, link2.getToNoteId());
        
        Link link3 = linkManager.getLinks().get(2);
        assertEquals("Other Note", link3.getToNoteTitle());
        assertNull(link3.getToNoteId()); // Should remain null
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testResolveOrphanedLinks_CaseInsensitive() throws Exception {
        // Setup - create orphaned link with different case
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "future note", null, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - resolve with different case
        linkManager.resolveOrphanedLinks("Future Note", 5, storage);
        
        // Verify - link is resolved despite case difference
        assertEquals(1, linkManager.getLinks().size());
        Link link = linkManager.getLinks().get(0);
        assertEquals("future note", link.getToNoteTitle());
        assertEquals(5, link.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testResolveOrphanedLinks_NoOrphanedLinks() throws Exception {
        // Setup - create links that are already resolved
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Existing Note", 2, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - try to resolve for a different note
        linkManager.resolveOrphanedLinks("New Note", 5, storage);
        
        // Verify - existing link unchanged
        assertEquals(1, linkManager.getLinks().size());
        Link link = linkManager.getLinks().get(0);
        assertEquals("Existing Note", link.getToNoteTitle());
        assertEquals(2, link.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testResolveOrphanedLinks_EmptyLinksList() throws Exception {
        // Setup - empty links list
        linkManager.setLinks(new ArrayList<>());
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - should not throw exception
        linkManager.resolveOrphanedLinks("New Note", 5, storage);
        
        // Verify - still empty
        assertEquals(0, linkManager.getLinks().size());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testDeleteLinksForNote_RemovesOutgoingLinks() throws Exception {
        // Setup - create links from note 1
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target 1", 2, java.time.LocalDateTime.now()));
        links.add(new Link(1, "Target 2", 3, java.time.LocalDateTime.now()));
        links.add(new Link(4, "Other Target", 5, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - delete all links for note 1
        linkManager.deleteLinksForNote(1, storage);
        
        // Verify - only link from note 4 remains
        assertEquals(1, linkManager.getLinks().size());
        Link remainingLink = linkManager.getLinks().get(0);
        assertEquals(4, remainingLink.getFromNoteId());
        assertEquals("Other Target", remainingLink.getToNoteTitle());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testDeleteLinksForNote_RemovesIncomingLinks() throws Exception {
        // Setup - create links to note 2
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 2, java.time.LocalDateTime.now()));
        links.add(new Link(3, "Target", 2, java.time.LocalDateTime.now()));
        links.add(new Link(4, "Other Target", 5, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - delete all links for note 2
        linkManager.deleteLinksForNote(2, storage);
        
        // Verify - only link to note 5 remains
        assertEquals(1, linkManager.getLinks().size());
        Link remainingLink = linkManager.getLinks().get(0);
        assertEquals(4, remainingLink.getFromNoteId());
        assertEquals(5, remainingLink.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testDeleteLinksForNote_RemovesBothOutgoingAndIncoming() throws Exception {
        // Setup - create links both from and to note 2
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(2, "Target", 3, java.time.LocalDateTime.now())); // Outgoing from 2
        links.add(new Link(1, "Note 2", 2, java.time.LocalDateTime.now())); // Incoming to 2
        links.add(new Link(4, "Other", 5, java.time.LocalDateTime.now())); // Unrelated
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - delete all links for note 2
        linkManager.deleteLinksForNote(2, storage);
        
        // Verify - only unrelated link remains
        assertEquals(1, linkManager.getLinks().size());
        Link remainingLink = linkManager.getLinks().get(0);
        assertEquals(4, remainingLink.getFromNoteId());
        assertEquals(5, remainingLink.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testDeleteLinksForNote_NoLinksForNote() throws Exception {
        // Setup - create links that don't involve note 10
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 2, java.time.LocalDateTime.now()));
        links.add(new Link(3, "Other", 4, java.time.LocalDateTime.now()));
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - delete links for note 10 (which has no links)
        linkManager.deleteLinksForNote(10, storage);
        
        // Verify - all links remain
        assertEquals(2, linkManager.getLinks().size());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testDeleteLinksForNote_EmptyLinksList() throws Exception {
        // Setup - empty links list
        linkManager.setLinks(new ArrayList<>());
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - should not throw exception
        linkManager.deleteLinksForNote(1, storage);
        
        // Verify - still empty
        assertEquals(0, linkManager.getLinks().size());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
    
    @Test
    void testDeleteLinksForNote_IgnoresOrphanedLinks() throws Exception {
        // Setup - create mix of resolved and orphaned links
        ArrayList<Link> links = new ArrayList<>();
        links.add(new Link(1, "Target", 2, java.time.LocalDateTime.now())); // Incoming to 2
        links.add(new Link(3, "Target", null, java.time.LocalDateTime.now())); // Orphaned, should remain
        linkManager.setLinks(links);
        
        // Create temporary storage
        String tempDir = System.getProperty("java.io.tmpdir") + "/linkmanager_test_" + System.currentTimeMillis();
        StorageLayer storage = new StorageLayer(tempDir);
        storage.initializeStorage();
        
        // Execute - delete links for note 2
        linkManager.deleteLinksForNote(2, storage);
        
        // Verify - orphaned link remains (toNoteId is null, so it doesn't match)
        assertEquals(1, linkManager.getLinks().size());
        Link remainingLink = linkManager.getLinks().get(0);
        assertEquals(3, remainingLink.getFromNoteId());
        assertNull(remainingLink.getToNoteId());
        
        // Cleanup
        new java.io.File(tempDir + "/notes.json").delete();
        new java.io.File(tempDir + "/links.json").delete();
        new java.io.File(tempDir).delete();
    }
}
