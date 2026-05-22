# Personal Knowledge Base

A CLI-based note management system with full-text search and automatic bidirectional linking, implemented in Java 11+.

## Features

### Core Functionality
- **CRUD Operations**: Create, read, update, and delete notes with titles, content, and tags
- **Persistent Storage**: JSON file-based storage with automatic saving
- **Tag-Based Organization**: Filter and organize notes using tags
- **Full-Text Search**: Advanced TF-IDF (Term Frequency-Inverse Document Frequency) search algorithm for relevant results
- **Bidirectional Linking**: Automatic wiki-style `[[Note Title]]` linking with backlink tracking

### Advanced Features
- **TF-IDF Search**: Intelligent ranking of search results based on term relevance
- **Snippet Generation**: Context-aware snippets with highlighted search terms
- **Orphaned Link Resolution**: Automatically resolves links when target notes are created
- **Link Navigation**: View both outgoing and incoming links for any note
- **Comprehensive Logging**: Application logs stored in `~/.persistent-mini-app/app.log`

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6+ (for building from source)

## Installation & Setup

### Option 1: Run Pre-built JAR

```bash
# Run the application
java -jar target/persistent-mini-app-1.0.jar
```

### Option 2: Build from Source

```bash
# Clone the repository
git clone <repository-url>
cd persistent-mini-app

# Build the project
mvn clean package -Dmaven.test.skip=true

# Run the application
java -jar target/persistent-mini-app-1.0.jar
```

### Custom Data Directory

By default, data is stored in `~/.persistent-mini-app/`. To use a custom directory:

```bash
java -jar target/persistent-mini-app-1.0.jar /path/to/custom/directory
```

## Usage

### Available Commands

Once the application starts, you'll see a command prompt. Here are the available commands:

#### Create a Note
```
> create
```
Follow the prompts to enter:
- Title
- Content (multi-line, end with `###` on a new line)
- Tags (comma-separated, optional)

#### Read a Note
```
> read <title>
```
Example:
```
> read My First Note
```

#### Update a Note
```
> update <title>
```
Follow the prompts to enter new content and tags.

#### Delete a Note
```
> delete <title>
```
Confirms before deletion.

#### List Notes
```
> list              # List all notes
> list <tag>        # List notes with specific tag
```
Examples:
```
> list
> list work
```

#### Search Notes
```
> search <query>
```
Returns ranked results with snippets and highlighted terms.

Example:
```
> search machine learning
```

#### View Links
```
> links <title>
```
Shows both outgoing links (notes this note links to) and incoming links (notes that link to this note).

Example:
```
> links Project Ideas
```

#### Help
```
> help
```
Displays available commands.

#### Exit
```
> exit
```
Saves all data and exits the application.

## Bidirectional Linking

Create links between notes using wiki-style syntax:

```
This note references [[Another Note]] and [[Yet Another Note]].
```

- Links are automatically detected and stored
- View backlinks using the `links` command
- Orphaned links (links to non-existent notes) are automatically resolved when the target note is created

## Search Features

The search engine uses TF-IDF algorithm to rank results:

1. **Term Frequency (TF)**: How often a search term appears in a note
2. **Inverse Document Frequency (IDF)**: How unique a term is across all notes
3. **Combined Score**: TF × IDF provides relevance ranking

Search results include:
- Relevance score
- Context snippet with highlighted terms
- Note tags

## Data Storage

- **Location**: `~/.persistent-mini-app/` (or custom directory)
- **Format**: JSON files
  - `notes.json`: All notes with metadata
  - `links.json`: Bidirectional link relationships
  - `app.log`: Application logs (with rotation)

## Examples

### Creating a Linked Note

```
> create
Title: Project Ideas
Content (enter '###' on a new line to finish):
I want to build a [[Personal Knowledge Base]] that helps me organize my thoughts.
It should integrate with [[My Research Notes]] for better context.
###
Tags (comma-separated, optional): projects, ideas

✓ Note created successfully!
```

### Searching

```
> search knowledge base
=== Search Results for: knowledge base ===

Found 2 result(s):

1. Project Ideas (score: 0.85)
   ...build a **Personal** **Knowledge** **Base** that helps me organize...
   Tags: [projects, ideas]

2. Personal Knowledge Base (score: 0.72)
   ...A CLI-based note management system with full-text search...
   Tags: [software, tools]
```

### Viewing Links

```
> links Project Ideas
=== Links for: Project Ideas ===

Outgoing links (2):
  → Personal Knowledge Base
  → My Research Notes

Incoming links (0):
  (none)
```

## Architecture

- **Storage Layer**: JSON file I/O with atomic writes
- **Note Manager**: CRUD operations with validation
- **Search Engine**: Custom TF-IDF implementation
- **Link Manager**: Bidirectional link tracking and resolution
- **CLI Interface**: Interactive REPL with command parsing

## Error Handling

The application includes comprehensive error handling:
- Input validation for titles, content, and tags
- Graceful handling of file I/O errors
- Atomic writes to prevent data corruption
- Detailed error messages for user guidance

## Logging

Logs are stored in `~/.persistent-mini-app/app.log` with:
- Automatic rotation (5 files, 10MB each)
- Timestamps and log levels
- Operation tracking and error details

## Contributing

This project was built as a personal knowledge management tool. Feel free to fork and customize for your needs.

## License

[Specify your license here]

## Support

For issues or questions, please [create an issue](link-to-issues) or contact [your-email].
