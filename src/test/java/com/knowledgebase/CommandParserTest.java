package com.knowledgebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandParser class.
 */
class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    // Test parsing valid CREATE command
    @Test
    void testParseCreateCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("create My Note Title");
        assertEquals(CommandType.CREATE, cmd.getAction());
        assertEquals("My Note Title", cmd.getArg("title"));
    }

    @Test
    void testParseCreateCommandWithExtraWhitespace() throws InvalidCommandException {
        Command cmd = parser.parseCommand("  create   My Note Title  ");
        assertEquals(CommandType.CREATE, cmd.getAction());
        assertEquals("My Note Title", cmd.getArg("title"));
    }

    @Test
    void testParseCreateCommandWithoutTitle() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("create")
        );
        assertTrue(exception.getMessage().contains("requires a title"));
    }

    @Test
    void testParseCreateCommandWithEmptyTitle() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("create   ")
        );
        assertTrue(exception.getMessage().contains("requires a title"));
    }

    // Test parsing valid READ command
    @Test
    void testParseReadCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("read My Note");
        assertEquals(CommandType.READ, cmd.getAction());
        assertEquals("My Note", cmd.getArg("title"));
    }

    @Test
    void testParseReadCommandWithoutTitle() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("read")
        );
        assertTrue(exception.getMessage().contains("requires a title"));
    }

    // Test parsing valid UPDATE command
    @Test
    void testParseUpdateCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("update My Note");
        assertEquals(CommandType.UPDATE, cmd.getAction());
        assertEquals("My Note", cmd.getArg("title"));
    }

    @Test
    void testParseUpdateCommandWithoutTitle() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("update")
        );
        assertTrue(exception.getMessage().contains("requires a title"));
    }

    // Test parsing valid DELETE command
    @Test
    void testParseDeleteCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("delete My Note");
        assertEquals(CommandType.DELETE, cmd.getAction());
        assertEquals("My Note", cmd.getArg("title"));
    }

    @Test
    void testParseDeleteCommandWithoutTitle() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("delete")
        );
        assertTrue(exception.getMessage().contains("requires a title"));
    }

    // Test parsing valid LIST command
    @Test
    void testParseListCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("list");
        assertEquals(CommandType.LIST, cmd.getAction());
        assertFalse(cmd.hasArg("tag"));
    }

    @Test
    void testParseListCommandWithTag() throws InvalidCommandException {
        Command cmd = parser.parseCommand("list --tag programming");
        assertEquals(CommandType.LIST, cmd.getAction());
        assertEquals("programming", cmd.getArg("tag"));
    }

    @Test
    void testParseListCommandWithTagMultipleWords() throws InvalidCommandException {
        Command cmd = parser.parseCommand("list --tag my tag");
        assertEquals(CommandType.LIST, cmd.getAction());
        assertEquals("my tag", cmd.getArg("tag"));
    }

    @Test
    void testParseListCommandWithTagButNoValue() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("list --tag")
        );
        assertTrue(exception.getMessage().contains("requires a tag value"));
    }

    @Test
    void testParseListCommandWithInvalidOption() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("list --invalid")
        );
        assertTrue(exception.getMessage().contains("Invalid list command syntax"));
    }

    // Test parsing valid SEARCH command
    @Test
    void testParseSearchCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("search java programming");
        assertEquals(CommandType.SEARCH, cmd.getAction());
        assertEquals("java programming", cmd.getArg("query"));
    }

    @Test
    void testParseSearchCommandWithoutQuery() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("search")
        );
        assertTrue(exception.getMessage().contains("requires a query"));
    }

    // Test parsing valid LINKS command
    @Test
    void testParseLinksCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("links My Note");
        assertEquals(CommandType.LINKS, cmd.getAction());
        assertEquals("My Note", cmd.getArg("title"));
    }

    @Test
    void testParseLinksCommandWithoutTitle() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("links")
        );
        assertTrue(exception.getMessage().contains("requires a title"));
    }

    // Test parsing valid EXIT command
    @Test
    void testParseExitCommand() throws InvalidCommandException {
        Command cmd = parser.parseCommand("exit");
        assertEquals(CommandType.EXIT, cmd.getAction());
    }

    @Test
    void testParseExitCommandCaseInsensitive() throws InvalidCommandException {
        Command cmd = parser.parseCommand("EXIT");
        assertEquals(CommandType.EXIT, cmd.getAction());
    }

    // Test edge cases
    @Test
    void testParseEmptyCommand() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("")
        );
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    void testParseNullCommand() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand(null)
        );
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    void testParseWhitespaceOnlyCommand() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("   ")
        );
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    void testParseUnknownCommand() {
        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> parser.parseCommand("unknown command")
        );
        assertTrue(exception.getMessage().contains("Unknown command"));
        assertTrue(exception.getMessage().contains("unknown"));
    }

    @Test
    void testParseCaseInsensitiveCommands() throws InvalidCommandException {
        Command cmd1 = parser.parseCommand("CREATE My Note");
        assertEquals(CommandType.CREATE, cmd1.getAction());

        Command cmd2 = parser.parseCommand("ReAd My Note");
        assertEquals(CommandType.READ, cmd2.getAction());

        Command cmd3 = parser.parseCommand("SEARCH query");
        assertEquals(CommandType.SEARCH, cmd3.getAction());
    }

    @Test
    void testParseCommandWithSpecialCharactersInTitle() throws InvalidCommandException {
        Command cmd = parser.parseCommand("create Note with @#$ special chars!");
        assertEquals(CommandType.CREATE, cmd.getAction());
        assertEquals("Note with @#$ special chars!", cmd.getArg("title"));
    }

    @Test
    void testParseCommandWithUnicodeCharacters() throws InvalidCommandException {
        Command cmd = parser.parseCommand("create 日本語のノート");
        assertEquals(CommandType.CREATE, cmd.getAction());
        assertEquals("日本語のノート", cmd.getArg("title"));
    }
}
