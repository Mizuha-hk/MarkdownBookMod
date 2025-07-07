package com.markdownbookmod.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MarkdownLexerTest {
    
    @Test
    fun `should tokenize simple text`() {
        val lexer = MarkdownLexer("Hello World")
        val tokens = lexer.tokenize()
        
        assertEquals(2, tokens.size)
        assertEquals(TokenType.TEXT, tokens[0].type)
        assertEquals("Hello World", tokens[0].value)
        assertEquals(TokenType.EOF, tokens[1].type)
    }
    
    @Test
    fun `should tokenize text with whitespace`() {
        val lexer = MarkdownLexer("Hello    World")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.TEXT, tokens[0].type)
        assertEquals("Hello", tokens[0].value)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals("    ", tokens[1].value)
        assertEquals(TokenType.TEXT, tokens[2].type)
        assertEquals("World", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize newlines`() {
        val lexer = MarkdownLexer("Hello\nWorld")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.TEXT, tokens[0].type)
        assertEquals("Hello", tokens[0].value)
        assertEquals(TokenType.NEWLINE, tokens[1].type)
        assertEquals("\n", tokens[1].value)
        assertEquals(TokenType.TEXT, tokens[2].type)
        assertEquals("World", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize heading level 1`() {
        val lexer = MarkdownLexer("# Title")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.HEADING_1, tokens[0].type)
        assertEquals("#", tokens[0].value)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(" ", tokens[1].value)
        assertEquals(TokenType.TEXT, tokens[2].type)
        assertEquals("Title", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize heading level 2`() {
        val lexer = MarkdownLexer("## Subtitle")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.HEADING_2, tokens[0].type)
        assertEquals("##", tokens[0].value)
    }
    
    @Test
    fun `should tokenize heading level 3`() {
        val lexer = MarkdownLexer("### Sub-subtitle")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.HEADING_3, tokens[0].type)
        assertEquals("###", tokens[0].value)
    }
    
    @Test
    fun `should tokenize heading level 4`() {
        val lexer = MarkdownLexer("#### Level 4")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.HEADING_4, tokens[0].type)
        assertEquals("####", tokens[0].value)
    }
    
    @Test
    fun `should tokenize heading level 5`() {
        val lexer = MarkdownLexer("##### Level 5")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.HEADING_5, tokens[0].type)
        assertEquals("#####", tokens[0].value)
    }
    
    @Test
    fun `should not tokenize heading without space`() {
        val lexer = MarkdownLexer("#NoSpace")
        val tokens = lexer.tokenize()
        
        assertEquals(2, tokens.size)
        assertEquals(TokenType.TEXT, tokens[0].type)
        assertEquals("#NoSpace", tokens[0].value)
        assertEquals(TokenType.EOF, tokens[1].type)
    }
    
    @Test
    fun `should not tokenize heading in middle of line`() {
        val lexer = MarkdownLexer("text # not heading")
        val tokens = lexer.tokenize()
        
        assertEquals(2, tokens.size)
        assertEquals(TokenType.TEXT, tokens[0].type)
        assertEquals("text # not heading", tokens[0].value)
        assertEquals(TokenType.EOF, tokens[1].type)
    }
    
    @Test
    fun `should tokenize bold text`() {
        val lexer = MarkdownLexer("**bold**")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.BOLD, tokens[0].type)
        assertEquals("**", tokens[0].value)
        assertEquals(TokenType.TEXT, tokens[1].type)
        assertEquals("bold", tokens[1].value)
        assertEquals(TokenType.BOLD, tokens[2].type)
        assertEquals("**", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize italic text`() {
        val lexer = MarkdownLexer("*italic*")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.ITALIC, tokens[0].type)
        assertEquals("*", tokens[0].value)
        assertEquals(TokenType.TEXT, tokens[1].type)
        assertEquals("italic", tokens[1].value)
        assertEquals(TokenType.ITALIC, tokens[2].type)
        assertEquals("*", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize strikethrough text`() {
        val lexer = MarkdownLexer("~~strikethrough~~")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.STRIKETHROUGH, tokens[0].type)
        assertEquals("~~", tokens[0].value)
        assertEquals(TokenType.TEXT, tokens[1].type)
        assertEquals("strikethrough", tokens[1].value)
        assertEquals(TokenType.STRIKETHROUGH, tokens[2].type)
        assertEquals("~~", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize bullet point with asterisk`() {
        val lexer = MarkdownLexer("* Item")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.BULLET_POINT, tokens[0].type)
        assertEquals("*", tokens[0].value)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(" ", tokens[1].value)
        assertEquals(TokenType.TEXT, tokens[2].type)
        assertEquals("Item", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize bullet point with dash`() {
        val lexer = MarkdownLexer("- Item")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.BULLET_POINT, tokens[0].type)
        assertEquals("-", tokens[0].value)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(" ", tokens[1].value)
        assertEquals(TokenType.TEXT, tokens[2].type)
        assertEquals("Item", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize numbered list`() {
        val lexer = MarkdownLexer("1. First item")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.NUMBERED_POINT, tokens[0].type)
        assertEquals("1.", tokens[0].value)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(" ", tokens[1].value)
        assertEquals(TokenType.TEXT, tokens[2].type)
        assertEquals("First item", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should tokenize multi-digit numbered list`() {
        val lexer = MarkdownLexer("10. Tenth item")
        val tokens = lexer.tokenize()
        
        assertEquals(4, tokens.size)
        assertEquals(TokenType.NUMBERED_POINT, tokens[0].type)
        assertEquals("10.", tokens[0].value)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(" ", tokens[1].value)
        assertEquals(TokenType.TEXT, tokens[2].type)
        assertEquals("Tenth item", tokens[2].value)
        assertEquals(TokenType.EOF, tokens[3].type)
    }
    
    @Test
    fun `should not tokenize number without dot and space`() {
        val lexer = MarkdownLexer("1.no space")
        val tokens = lexer.tokenize()
        
        assertEquals(2, tokens.size)
        assertEquals(TokenType.TEXT, tokens[0].type)
        assertEquals("1.no space", tokens[0].value)
        assertEquals(TokenType.EOF, tokens[1].type)
    }
    
    @Test
    fun `should handle empty input`() {
        val lexer = MarkdownLexer("")
        val tokens = lexer.tokenize()
        
        assertEquals(1, tokens.size)
        assertEquals(TokenType.EOF, tokens[0].type)
    }
    
    @Test
    fun `should handle complex markdown`() {
        val lexer = MarkdownLexer("# Title\n\n**Bold** and *italic* text\n\n- Item 1\n- Item 2")
        val tokens = lexer.tokenize()
        
        // Verify first heading token
        assertEquals(TokenType.HEADING_1, tokens[0].type)
        assertEquals("#", tokens[0].value)
        
        // Should end with EOF
        assertEquals(TokenType.EOF, tokens.last().type)
        
        // Should contain various token types
        val tokenTypes = tokens.map { it.type }.toSet()
        assertTrue(tokenTypes.contains(TokenType.HEADING_1))
        assertTrue(tokenTypes.contains(TokenType.BOLD))
        assertTrue(tokenTypes.contains(TokenType.ITALIC))
        assertTrue(tokenTypes.contains(TokenType.BULLET_POINT))
        assertTrue(tokenTypes.contains(TokenType.TEXT))
        assertTrue(tokenTypes.contains(TokenType.NEWLINE))
        assertTrue(tokenTypes.contains(TokenType.WHITESPACE))
        assertTrue(tokenTypes.contains(TokenType.EOF))
    }
    
    @Test
    fun `should handle line positioning`() {
        val lexer = MarkdownLexer("Line 1\nLine 2")
        val tokens = lexer.tokenize()
        
        assertEquals(1, tokens[0].line) // Line 1 text
        assertEquals(1, tokens[1].line) // Newline
        assertEquals(2, tokens[2].line) // Line 2 text
    }
}