package com.knowledgebase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Note validation methods.
 * Tests validation rules for title, content, and tags.
 */
class NoteValidationTest {
    
    // Title validation tests
    
    @Test
    @DisplayName("Valid title should pass validation")
    void testValidTitle() {
        assertDoesNotThrow(() -> Note.validateTitle("Valid Title"));
        assertDoesNotThrow(() -> Note.validateTitle("A"));
        assertDoesNotThrow(() -> Note.validateTitle("Title with spaces and numbers 123"));
    }
    
    @Test
    @DisplayName("Null title should throw ValidationException")
    void testNullTitle() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTitle(null));
        assertEquals("Title cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Empty title should throw ValidationException")
    void testEmptyTitle() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTitle(""));
        assertEquals("Title cannot be empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Title with leading whitespace should throw ValidationException")
    void testTitleWithLeadingWhitespace() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTitle("  Title"));
        assertEquals("Title cannot have leading or trailing whitespace", exception.getMessage());
    }
    
    @Test
    @DisplayName("Title with trailing whitespace should throw ValidationException")
    void testTitleWithTrailingWhitespace() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTitle("Title  "));
        assertEquals("Title cannot have leading or trailing whitespace", exception.getMessage());
    }
    
    @Test
    @DisplayName("Title exceeding 200 characters should throw ValidationException")
    void testTitleTooLong() {
        String longTitle = "a".repeat(201);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTitle(longTitle));
        assertTrue(exception.getMessage().contains("cannot exceed 200 characters"));
        assertTrue(exception.getMessage().contains("201"));
    }
    
    @Test
    @DisplayName("Title with exactly 200 characters should pass validation")
    void testTitleExactly200Chars() {
        String title = "a".repeat(200);
        assertDoesNotThrow(() -> Note.validateTitle(title));
    }
    
    // Title uniqueness validation tests
    
    @Test
    @DisplayName("Unique title should pass uniqueness validation")
    void testUniqueTitleValidation() {
        List<String> existingTitles = Arrays.asList("Title One", "Title Two", "Title Three");
        assertDoesNotThrow(() -> Note.validateTitleUniqueness("New Title", existingTitles));
    }
    
    @Test
    @DisplayName("Duplicate title (exact match) should throw ValidationException")
    void testDuplicateTitleExactMatch() {
        List<String> existingTitles = Arrays.asList("Title One", "Title Two", "Title Three");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTitleUniqueness("Title Two", existingTitles));
        assertTrue(exception.getMessage().contains("must be unique"));
        assertTrue(exception.getMessage().contains("Title Two"));
    }
    
    @Test
    @DisplayName("Duplicate title (case-insensitive) should throw ValidationException")
    void testDuplicateTitleCaseInsensitive() {
        List<String> existingTitles = Arrays.asList("Title One", "Title Two", "Title Three");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTitleUniqueness("title two", existingTitles));
        assertTrue(exception.getMessage().contains("must be unique"));
    }
    
    @Test
    @DisplayName("Null title or null list should not throw exception")
    void testNullTitleUniquenessValidation() {
        assertDoesNotThrow(() -> Note.validateTitleUniqueness(null, Arrays.asList("Title")));
        assertDoesNotThrow(() -> Note.validateTitleUniqueness("Title", null));
        assertDoesNotThrow(() -> Note.validateTitleUniqueness(null, null));
    }
    
    // Content validation tests
    
    @Test
    @DisplayName("Valid content should pass validation")
    void testValidContent() {
        assertDoesNotThrow(() -> Note.validateContent("Valid content"));
        assertDoesNotThrow(() -> Note.validateContent("A"));
        assertDoesNotThrow(() -> Note.validateContent("Content with\nmultiple\nlines"));
    }
    
    @Test
    @DisplayName("Null content should throw ValidationException")
    void testNullContent() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateContent(null));
        assertEquals("Content cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Empty content should throw ValidationException")
    void testEmptyContent() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateContent(""));
        assertEquals("Content cannot be empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Content exceeding 100,000 characters should throw ValidationException")
    void testContentTooLong() {
        String longContent = "a".repeat(100001);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateContent(longContent));
        assertTrue(exception.getMessage().contains("cannot exceed 100,000 characters"));
        assertTrue(exception.getMessage().contains("100001"));
    }
    
    @Test
    @DisplayName("Content with exactly 100,000 characters should pass validation")
    void testContentExactly100kChars() {
        String content = "a".repeat(100000);
        assertDoesNotThrow(() -> Note.validateContent(content));
    }
    
    // Tags validation tests
    
    @Test
    @DisplayName("Valid tags should pass validation")
    void testValidTags() {
        assertDoesNotThrow(() -> Note.validateTags(Arrays.asList("tag1", "tag2", "tag3")));
        assertDoesNotThrow(() -> Note.validateTags(Arrays.asList("a")));
        assertDoesNotThrow(() -> Note.validateTags(Arrays.asList("tag-with-hyphens")));
        assertDoesNotThrow(() -> Note.validateTags(Arrays.asList("TAG123")));
    }
    
    @Test
    @DisplayName("Null tags list should pass validation (tags are optional)")
    void testNullTags() {
        assertDoesNotThrow(() -> Note.validateTags(null));
    }
    
    @Test
    @DisplayName("Empty tags list should pass validation")
    void testEmptyTagsList() {
        assertDoesNotThrow(() -> Note.validateTags(Collections.emptyList()));
    }
    
    @Test
    @DisplayName("More than 10 tags should throw ValidationException")
    void testTooManyTags() {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5", 
                                          "tag6", "tag7", "tag8", "tag9", "tag10", "tag11");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTags(tags));
        assertTrue(exception.getMessage().contains("Cannot have more than 10 tags"));
        assertTrue(exception.getMessage().contains("11"));
    }
    
    @Test
    @DisplayName("Exactly 10 tags should pass validation")
    void testExactly10Tags() {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5", 
                                          "tag6", "tag7", "tag8", "tag9", "tag10");
        assertDoesNotThrow(() -> Note.validateTags(tags));
    }
    
    @Test
    @DisplayName("Null tag in list should throw ValidationException")
    void testNullTagInList() {
        List<String> tags = Arrays.asList("tag1", null, "tag3");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTags(tags));
        assertTrue(exception.getMessage().contains("Tag at index 1 cannot be null"));
    }
    
    @Test
    @DisplayName("Empty tag in list should throw ValidationException")
    void testEmptyTagInList() {
        List<String> tags = Arrays.asList("tag1", "", "tag3");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTags(tags));
        assertTrue(exception.getMessage().contains("Tag at index 1 cannot be empty"));
    }
    
    @Test
    @DisplayName("Tag exceeding 50 characters should throw ValidationException")
    void testTagTooLong() {
        String longTag = "a".repeat(51);
        List<String> tags = Arrays.asList("tag1", longTag);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateTags(tags));
        assertTrue(exception.getMessage().contains("Tag at index 1 cannot exceed 50 characters"));
        assertTrue(exception.getMessage().contains("51"));
    }
    
    @Test
    @DisplayName("Tag with exactly 50 characters should pass validation")
    void testTagExactly50Chars() {
        String tag = "a".repeat(50);
        assertDoesNotThrow(() -> Note.validateTags(Arrays.asList(tag)));
    }
    
    @Test
    @DisplayName("Tag with invalid characters should throw ValidationException")
    void testTagWithInvalidCharacters() {
        List<String> invalidTags = Arrays.asList(
            Arrays.asList("tag with spaces"),
            Arrays.asList("tag_with_underscores"),
            Arrays.asList("tag.with.dots"),
            Arrays.asList("tag@special"),
            Arrays.asList("tag#hash")
        );
        
        for (List<String> tags : invalidTags) {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> Note.validateTags(tags));
            assertTrue(exception.getMessage().contains("must contain only alphanumeric characters and hyphens"));
        }
    }
    
    // Combined validation tests
    
    @Test
    @DisplayName("Valid note data should pass combined validation")
    void testValidNoteValidation() {
        assertDoesNotThrow(() -> Note.validateNote("Valid Title", "Valid content", 
            Arrays.asList("tag1", "tag2")));
    }
    
    @Test
    @DisplayName("Invalid title in combined validation should throw ValidationException")
    void testInvalidTitleInCombinedValidation() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateNote("", "Valid content", Arrays.asList("tag1")));
        assertTrue(exception.getMessage().contains("Title cannot be empty"));
    }
    
    @Test
    @DisplayName("Invalid content in combined validation should throw ValidationException")
    void testInvalidContentInCombinedValidation() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateNote("Valid Title", "", Arrays.asList("tag1")));
        assertTrue(exception.getMessage().contains("Content cannot be empty"));
    }
    
    @Test
    @DisplayName("Invalid tags in combined validation should throw ValidationException")
    void testInvalidTagsInCombinedValidation() {
        List<String> tooManyTags = Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5", 
                                                 "tag6", "tag7", "tag8", "tag9", "tag10", "tag11");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> Note.validateNote("Valid Title", "Valid content", tooManyTags));
        assertTrue(exception.getMessage().contains("Cannot have more than 10 tags"));
    }
}
