package com.markdownbookmod.core

import com.markdownbookmod.core.models.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MarkdownParserTest {
    
    @Test
    fun `should parse empty document`() {
        val tokens = listOf(Token(TokenType.EOF, "", 0, 1))
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertNotNull(document)
        assertTrue(document.children.isEmpty())
    }
    
    @Test
    fun `should parse simple text as paragraph`() {
        val tokens = listOf(
            Token(TokenType.TEXT, "Hello", 0, 1),
            Token(TokenType.WHITESPACE, " ", 5, 1),
            Token(TokenType.TEXT, "World", 6, 1),
            Token(TokenType.EOF, "", 11, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Paragraph)
        
        val paragraph = document.children[0] as Paragraph
        assertEquals(3, paragraph.content.size)
        assertTrue(paragraph.content[0] is Text)
        assertTrue(paragraph.content[1] is Text) // whitespace
        assertTrue(paragraph.content[2] is Text)
        
        assertEquals("Hello", (paragraph.content[0] as Text).content)
        assertEquals(" ", (paragraph.content[1] as Text).content)
        assertEquals("World", (paragraph.content[2] as Text).content)
    }
    
    @Test
    fun `should parse heading level 1`() {
        val tokens = listOf(
            Token(TokenType.HEADING_1, "#", 0, 1),
            Token(TokenType.WHITESPACE, " ", 1, 1),
            Token(TokenType.TEXT, "Title", 2, 1),
            Token(TokenType.EOF, "", 7, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Heading)
        
        val heading = document.children[0] as Heading
        assertEquals(1, heading.level)
        assertEquals("Title", heading.text)
    }
    
    @Test
    fun `should parse heading level 2`() {
        val tokens = listOf(
            Token(TokenType.HEADING_2, "##", 0, 1),
            Token(TokenType.WHITESPACE, " ", 2, 1),
            Token(TokenType.TEXT, "Subtitle", 3, 1),
            Token(TokenType.EOF, "", 11, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Heading)
        
        val heading = document.children[0] as Heading
        assertEquals(2, heading.level)
        assertEquals("Subtitle", heading.text)
    }
    
    @Test
    fun `should parse heading level 3`() {
        val tokens = listOf(
            Token(TokenType.HEADING_3, "###", 0, 1),
            Token(TokenType.WHITESPACE, " ", 3, 1),
            Token(TokenType.TEXT, "Sub-subtitle", 4, 1),
            Token(TokenType.EOF, "", 16, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Heading)
        
        val heading = document.children[0] as Heading
        assertEquals(3, heading.level)
        assertEquals("Sub-subtitle", heading.text)
    }
    
    @Test
    fun `should parse heading with multiple text tokens`() {
        val tokens = listOf(
            Token(TokenType.HEADING_1, "#", 0, 1),
            Token(TokenType.WHITESPACE, " ", 1, 1),
            Token(TokenType.TEXT, "Multiple", 2, 1),
            Token(TokenType.TEXT, "Words", 10, 1),
            Token(TokenType.TEXT, "Title", 15, 1),
            Token(TokenType.EOF, "", 20, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Heading)
        
        val heading = document.children[0] as Heading
        assertEquals(1, heading.level)
        assertEquals("MultipleWordsTitle", heading.text)
    }
    
    @Test
    fun `should parse bullet list with asterisk`() {
        val tokens = listOf(
            Token(TokenType.BULLET_POINT, "*", 0, 1),
            Token(TokenType.WHITESPACE, " ", 1, 1),
            Token(TokenType.TEXT, "Item", 2, 1),
            Token(TokenType.WHITESPACE, " ", 6, 1),
            Token(TokenType.TEXT, "1", 7, 1),
            Token(TokenType.EOF, "", 8, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is BulletList)
        
        val bulletList = document.children[0] as BulletList
        assertEquals(1, bulletList.items.size)
        
        val item = bulletList.items[0]
        assertEquals(3, item.content.size)
        assertTrue(item.content[0] is Text)
        assertTrue(item.content[1] is Text) // whitespace
        assertTrue(item.content[2] is Text)
        
        assertEquals("Item", (item.content[0] as Text).content)
        assertEquals(" ", (item.content[1] as Text).content)
        assertEquals("1", (item.content[2] as Text).content)
    }
    
    @Test
    fun `should parse bullet list with multiple items`() {
        val tokens = listOf(
            Token(TokenType.BULLET_POINT, "*", 0, 1),
            Token(TokenType.WHITESPACE, " ", 1, 1),
            Token(TokenType.TEXT, "Item", 2, 1),
            Token(TokenType.WHITESPACE, " ", 6, 1),
            Token(TokenType.TEXT, "1", 7, 1),
            Token(TokenType.NEWLINE, "\n", 8, 1),
            Token(TokenType.BULLET_POINT, "*", 9, 2),
            Token(TokenType.WHITESPACE, " ", 10, 2),
            Token(TokenType.TEXT, "Item", 11, 2),
            Token(TokenType.WHITESPACE, " ", 15, 2),
            Token(TokenType.TEXT, "2", 16, 2),
            Token(TokenType.EOF, "", 17, 2)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is BulletList)
        
        val bulletList = document.children[0] as BulletList
        assertEquals(2, bulletList.items.size)
        
        val item1 = bulletList.items[0]
        assertEquals("Item", (item1.content[0] as Text).content)
        assertEquals("1", (item1.content[2] as Text).content)
        
        val item2 = bulletList.items[1]
        assertEquals("Item", (item2.content[0] as Text).content)
        assertEquals("2", (item2.content[2] as Text).content)
    }
    
    @Test
    fun `should parse ordered list`() {
        val tokens = listOf(
            Token(TokenType.NUMBERED_POINT, "1.", 0, 1),
            Token(TokenType.WHITESPACE, " ", 2, 1),
            Token(TokenType.TEXT, "First", 3, 1),
            Token(TokenType.WHITESPACE, " ", 8, 1),
            Token(TokenType.TEXT, "item", 9, 1),
            Token(TokenType.EOF, "", 13, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is OrderedList)
        
        val orderedList = document.children[0] as OrderedList
        assertEquals(1, orderedList.items.size)
        
        val item = orderedList.items[0]
        assertEquals(3, item.content.size)
        assertEquals("First", (item.content[0] as Text).content)
        assertEquals(" ", (item.content[1] as Text).content)
        assertEquals("item", (item.content[2] as Text).content)
    }
    
    @Test
    fun `should parse ordered list with multiple items`() {
        val tokens = listOf(
            Token(TokenType.NUMBERED_POINT, "1.", 0, 1),
            Token(TokenType.WHITESPACE, " ", 2, 1),
            Token(TokenType.TEXT, "First", 3, 1),
            Token(TokenType.NEWLINE, "\n", 8, 1),
            Token(TokenType.NUMBERED_POINT, "2.", 9, 2),
            Token(TokenType.WHITESPACE, " ", 11, 2),
            Token(TokenType.TEXT, "Second", 12, 2),
            Token(TokenType.EOF, "", 18, 2)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is OrderedList)
        
        val orderedList = document.children[0] as OrderedList
        assertEquals(2, orderedList.items.size)
        
        assertEquals("First", (orderedList.items[0].content[0] as Text).content)
        assertEquals("Second", (orderedList.items[1].content[0] as Text).content)
    }
    
    @Test
    fun `should parse bold text`() {
        val tokens = listOf(
            Token(TokenType.BOLD, "**", 0, 1),
            Token(TokenType.TEXT, "bold", 2, 1),
            Token(TokenType.BOLD, "**", 6, 1),
            Token(TokenType.EOF, "", 8, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Paragraph)
        
        val paragraph = document.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertTrue(paragraph.content[0] is Bold)
        
        val bold = paragraph.content[0] as Bold
        assertEquals(1, bold.content.size)
        assertTrue(bold.content[0] is Text)
        assertEquals("bold", (bold.content[0] as Text).content)
    }
    
    @Test
    fun `should parse italic text`() {
        val tokens = listOf(
            Token(TokenType.ITALIC, "*", 0, 1),
            Token(TokenType.TEXT, "italic", 1, 1),
            Token(TokenType.ITALIC, "*", 7, 1),
            Token(TokenType.EOF, "", 8, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Paragraph)
        
        val paragraph = document.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertTrue(paragraph.content[0] is Italic)
        
        val italic = paragraph.content[0] as Italic
        assertEquals(1, italic.content.size)
        assertTrue(italic.content[0] is Text)
        assertEquals("italic", (italic.content[0] as Text).content)
    }
    
    @Test
    fun `should parse strikethrough text`() {
        val tokens = listOf(
            Token(TokenType.STRIKETHROUGH, "~~", 0, 1),
            Token(TokenType.TEXT, "strikethrough", 2, 1),
            Token(TokenType.STRIKETHROUGH, "~~", 15, 1),
            Token(TokenType.EOF, "", 17, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Paragraph)
        
        val paragraph = document.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertTrue(paragraph.content[0] is Strikethrough)
        
        val strikethrough = paragraph.content[0] as Strikethrough
        assertEquals(1, strikethrough.content.size)
        assertTrue(strikethrough.content[0] is Text)
        assertEquals("strikethrough", (strikethrough.content[0] as Text).content)
    }
    
    @Test
    fun `should parse mixed inline formatting`() {
        val tokens = listOf(
            Token(TokenType.TEXT, "Normal", 0, 1),
            Token(TokenType.WHITESPACE, " ", 6, 1),
            Token(TokenType.BOLD, "**", 7, 1),
            Token(TokenType.TEXT, "bold", 9, 1),
            Token(TokenType.BOLD, "**", 13, 1),
            Token(TokenType.WHITESPACE, " ", 15, 1),
            Token(TokenType.ITALIC, "*", 16, 1),
            Token(TokenType.TEXT, "italic", 17, 1),
            Token(TokenType.ITALIC, "*", 23, 1),
            Token(TokenType.EOF, "", 24, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Paragraph)
        
        val paragraph = document.children[0] as Paragraph
        assertEquals(5, paragraph.content.size)
        
        assertTrue(paragraph.content[0] is Text)
        assertEquals("Normal", (paragraph.content[0] as Text).content)
        
        assertTrue(paragraph.content[1] is Text) // whitespace
        assertEquals(" ", (paragraph.content[1] as Text).content)
        
        assertTrue(paragraph.content[2] is Bold)
        val bold = paragraph.content[2] as Bold
        assertEquals("bold", (bold.content[0] as Text).content)
        
        assertTrue(paragraph.content[3] is Text) // whitespace
        assertEquals(" ", (paragraph.content[3] as Text).content)
        
        assertTrue(paragraph.content[4] is Italic)
        val italic = paragraph.content[4] as Italic
        assertEquals("italic", (italic.content[0] as Text).content)
    }
    
    @Test
    fun `should handle unclosed bold formatting`() {
        val tokens = listOf(
            Token(TokenType.BOLD, "**", 0, 1),
            Token(TokenType.TEXT, "unclosed", 2, 1),
            Token(TokenType.EOF, "", 10, 1)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Paragraph)
        
        val paragraph = document.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertTrue(paragraph.content[0] is Bold)
        
        val bold = paragraph.content[0] as Bold
        assertEquals(1, bold.content.size)
        assertEquals("unclosed", (bold.content[0] as Text).content)
    }
    
    @Test
    fun `should skip whitespace and newlines at document level`() {
        val tokens = listOf(
            Token(TokenType.WHITESPACE, "  ", 0, 1),
            Token(TokenType.NEWLINE, "\n", 2, 1),
            Token(TokenType.WHITESPACE, "  ", 3, 2),
            Token(TokenType.TEXT, "content", 5, 2),
            Token(TokenType.EOF, "", 12, 2)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(1, document.children.size)
        assertTrue(document.children[0] is Paragraph)
        
        val paragraph = document.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertEquals("content", (paragraph.content[0] as Text).content)
    }
    
    @Test
    fun `should parse complex document with multiple elements`() {
        val tokens = listOf(
            Token(TokenType.HEADING_1, "#", 0, 1),
            Token(TokenType.WHITESPACE, " ", 1, 1),
            Token(TokenType.TEXT, "Title", 2, 1),
            Token(TokenType.NEWLINE, "\n", 7, 1),
            Token(TokenType.NEWLINE, "\n", 8, 2),
            Token(TokenType.TEXT, "Paragraph", 9, 3),
            Token(TokenType.WHITESPACE, " ", 18, 3),
            Token(TokenType.TEXT, "text", 19, 3),
            Token(TokenType.NEWLINE, "\n", 23, 3),
            Token(TokenType.NEWLINE, "\n", 24, 4),
            Token(TokenType.BULLET_POINT, "*", 25, 5),
            Token(TokenType.WHITESPACE, " ", 26, 5),
            Token(TokenType.TEXT, "Item", 27, 5),
            Token(TokenType.EOF, "", 31, 5)
        )
        val parser = MarkdownParser(tokens)
        val document = parser.parse()
        
        assertEquals(3, document.children.size)
        
        // First element should be heading
        assertTrue(document.children[0] is Heading)
        val heading = document.children[0] as Heading
        assertEquals(1, heading.level)
        assertEquals("Title", heading.text)
        
        // Second element should be paragraph
        assertTrue(document.children[1] is Paragraph)
        val paragraph = document.children[1] as Paragraph
        assertEquals(3, paragraph.content.size)
        assertEquals("Paragraph", (paragraph.content[0] as Text).content)
        
        // Third element should be bullet list
        assertTrue(document.children[2] is BulletList)
        val bulletList = document.children[2] as BulletList
        assertEquals(1, bulletList.items.size)
        assertEquals("Item", (bulletList.items[0].content[0] as Text).content)
    }
}