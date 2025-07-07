package com.markdownbookmod.core

import com.markdownbookmod.core.models.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MarkdownProcessorTest {
    
    private val processor = MarkdownProcessor()
    
    @Test
    fun `should parse simple text`() {
        val result = processor.parse("Hello World")
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Paragraph)
        
        val paragraph = result.children[0] as Paragraph
        assertEquals(3, paragraph.content.size) // "Hello", " ", "World"
        assertTrue(paragraph.content[0] is Text)
        assertEquals("Hello", (paragraph.content[0] as Text).content)
        assertTrue(paragraph.content[1] is Text) // whitespace
        assertEquals(" ", (paragraph.content[1] as Text).content)
        assertTrue(paragraph.content[2] is Text)
        assertEquals("World", (paragraph.content[2] as Text).content)
    }
    
    @Test
    fun `should parse empty string`() {
        val result = processor.parse("")
        
        assertNotNull(result)
        assertTrue(result.children.isEmpty())
    }
    
    @Test
    fun `should parse heading`() {
        val result = processor.parse("# Main Title")
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Heading)
        
        val heading = result.children[0] as Heading
        assertEquals(1, heading.level)
        assertEquals("Main Title", heading.text)
    }
    
    @Test
    fun `should parse multiple heading levels`() {
        val markdown = """
            # Level 1
            ## Level 2
            ### Level 3
            #### Level 4
            ##### Level 5
        """.trimIndent()
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertEquals(5, result.children.size)
        
        for (i in 0..4) {
            assertTrue(result.children[i] is Heading)
            val heading = result.children[i] as Heading
            assertEquals(i + 1, heading.level)
            assertEquals("Level ${i + 1}", heading.text)
        }
    }
    
    @Test
    fun `should parse bold text`() {
        val result = processor.parse("**bold text**")
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Paragraph)
        
        val paragraph = result.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertTrue(paragraph.content[0] is Bold)
        
        val bold = paragraph.content[0] as Bold
        assertEquals(1, bold.content.size)
        assertTrue(bold.content[0] is Text)
        assertEquals("bold text", (bold.content[0] as Text).content)
    }
    
    @Test
    fun `should parse italic text`() {
        val result = processor.parse("*italic text*")
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Paragraph)
        
        val paragraph = result.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertTrue(paragraph.content[0] is Italic)
        
        val italic = paragraph.content[0] as Italic
        assertEquals(1, italic.content.size)
        assertTrue(italic.content[0] is Text)
        assertEquals("italic text", (italic.content[0] as Text).content)
    }
    
    @Test
    fun `should parse strikethrough text`() {
        val result = processor.parse("~~strikethrough text~~")
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Paragraph)
        
        val paragraph = result.children[0] as Paragraph
        assertEquals(1, paragraph.content.size)
        assertTrue(paragraph.content[0] is Strikethrough)
        
        val strikethrough = paragraph.content[0] as Strikethrough
        assertEquals(1, strikethrough.content.size)
        assertTrue(strikethrough.content[0] is Text)
        assertEquals("strikethrough text", (strikethrough.content[0] as Text).content)
    }
    
    @Test
    fun `should parse bullet list`() {
        val markdown = """
            * Item 1
            * Item 2
            * Item 3
        """.trimIndent()
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is BulletList)
        
        val bulletList = result.children[0] as BulletList
        assertEquals(3, bulletList.items.size)
        
        assertEquals("Item 1", (bulletList.items[0].content[0] as Text).content)
        assertEquals("Item 2", (bulletList.items[1].content[0] as Text).content)
        assertEquals("Item 3", (bulletList.items[2].content[0] as Text).content)
    }
    
    @Test
    fun `should parse bullet list with dash`() {
        val markdown = """
            - Item 1
            - Item 2
            - Item 3
        """.trimIndent()
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is BulletList)
        
        val bulletList = result.children[0] as BulletList
        assertEquals(3, bulletList.items.size)
        
        assertEquals("Item 1", (bulletList.items[0].content[0] as Text).content)
        assertEquals("Item 2", (bulletList.items[1].content[0] as Text).content)
        assertEquals("Item 3", (bulletList.items[2].content[0] as Text).content)
    }
    
    @Test
    fun `should parse ordered list`() {
        val markdown = """
            1. First item
            2. Second item
            3. Third item
        """.trimIndent()
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is OrderedList)
        
        val orderedList = result.children[0] as OrderedList
        assertEquals(3, orderedList.items.size)
        
        assertEquals("First item", (orderedList.items[0].content[0] as Text).content)
        assertEquals("Second item", (orderedList.items[1].content[0] as Text).content)
        assertEquals("Third item", (orderedList.items[2].content[0] as Text).content)
    }
    
    @Test
    fun `should parse mixed formatting`() {
        val markdown = "Normal **bold** and *italic* text with ~~strikethrough~~"
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Paragraph)
        
        val paragraph = result.children[0] as Paragraph
        assertTrue(paragraph.content.size >= 7) // Should contain various elements
        
        // Check for presence of different formatting types
        val hasText = paragraph.content.any { it is Text && (it as Text).content == "Normal" }
        val hasBold = paragraph.content.any { it is Bold }
        val hasItalic = paragraph.content.any { it is Italic }
        val hasStrikethrough = paragraph.content.any { it is Strikethrough }
        
        assertTrue(hasText, "Should contain normal text")
        assertTrue(hasBold, "Should contain bold text")
        assertTrue(hasItalic, "Should contain italic text")
        assertTrue(hasStrikethrough, "Should contain strikethrough text")
    }
    
    @Test
    fun `should parse complex document`() {
        val markdown = """
            # Main Title
            
            This is a paragraph with **bold** text.
            
            ## Subtitle
            
            * Bullet item 1
            * Bullet item 2
            
            1. Ordered item 1
            2. Ordered item 2
            
            Another paragraph with *italic* and ~~strikethrough~~ text.
        """.trimIndent()
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertTrue(result.children.size >= 5) // At least heading, paragraph, subtitle, bullet list, ordered list, final paragraph
        
        // Check first element is main title
        assertTrue(result.children[0] is Heading)
        val mainTitle = result.children[0] as Heading
        assertEquals(1, mainTitle.level)
        assertEquals("Main Title", mainTitle.text)
        
        // Check for presence of different element types
        val hasHeading = result.children.any { it is Heading && (it as Heading).level == 2 }
        val hasParagraph = result.children.any { it is Paragraph }
        val hasBulletList = result.children.any { it is BulletList }
        val hasOrderedList = result.children.any { it is OrderedList }
        
        assertTrue(hasHeading, "Should contain level 2 heading")
        assertTrue(hasParagraph, "Should contain paragraphs")
        assertTrue(hasBulletList, "Should contain bullet list")
        assertTrue(hasOrderedList, "Should contain ordered list")
    }
    
    @Test
    fun `should handle invalid markdown gracefully`() {
        val markdown = "##invalid heading\n*unclosed italic\n**unclosed bold"
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        // Should not throw exception and return some result
        assertTrue(result.children.isNotEmpty())
    }
    
    @Test
    fun `should handle edge cases`() {
        // Test various edge cases
        val edgeCases = listOf(
            "#", // Just hash
            "# ", // Hash with space but no text
            "* ", // Bullet with space but no text
            "1. ", // Number with space but no text
            "**", // Just bold markers
            "~~", // Just strikethrough markers
            "\n\n\n", // Multiple newlines
            "   ", // Just whitespace
        )
        
        edgeCases.forEach { markdown ->
            val result = processor.parse(markdown)
            assertNotNull(result, "Should handle '$markdown' gracefully")
        }
    }
    
    @Test
    fun `should parse to minecraft text`() {
        val markdown = "**Bold** text"
        
        val result = processor.parseToMinecraftText(markdown)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        // The actual format depends on MinecraftComponentRenderer implementation
        // Just verify it doesn't throw and returns something
    }
    
    @Test
    fun `should handle error in parsing gracefully`() {
        // Create a scenario that might cause parsing errors
        // For now, we'll test with malformed input
        val malformedMarkdown = String(byteArrayOf(-1, -2, -3)) // Invalid UTF-8
        
        val result = processor.parse(malformedMarkdown)
        
        assertNotNull(result)
        // Should fallback to plain text on error
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Text)
    }
    
    @Test
    fun `should handle very long input`() {
        val longText = "a".repeat(10000)
        val markdown = "# $longText"
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Heading)
        
        val heading = result.children[0] as Heading
        assertEquals(longText, heading.text)
    }
    
    @Test
    fun `should handle nested formatting correctly`() {
        // Test that parser handles various combinations properly
        val testCases = mapOf(
            "***bold and italic***" to "Mixed bold/italic",
            "**bold with *italic* inside**" to "Nested formatting",
            "~~strikethrough with **bold** inside~~" to "Strikethrough with bold"
        )
        
        testCases.forEach { (markdown, description) ->
            val result = processor.parse(markdown)
            assertNotNull(result, "Should parse: $description")
            assertTrue(result.children.isNotEmpty(), "Should have content for: $description")
        }
    }
    
    @Test
    fun `should maintain whitespace properly`() {
        val markdown = "Word   with    multiple   spaces"
        
        val result = processor.parse(markdown)
        
        assertNotNull(result)
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is Paragraph)
        
        val paragraph = result.children[0] as Paragraph
        // Should preserve the whitespace structure
        val hasMultipleSpaces = paragraph.content.any { 
            it is Text && (it as Text).content.contains("   ")
        }
        assertTrue(hasMultipleSpaces, "Should preserve multiple spaces")
    }
}