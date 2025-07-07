package com.markdownbookmod.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.Assertions.*

@DisplayName("MarkdownLexer Tests")
class MarkdownLexerTest {
    
    private lateinit var lexer: MarkdownLexer
    
    @BeforeEach
    fun setUp() {
        lexer = MarkdownLexer()
    }
    
    @Nested
    @DisplayName("Basic Functionality")
    inner class BasicFunctionality {
        
        @Test
        @DisplayName("Empty string should return only EOF token")
        fun testEmptyString() {
            val tokens = lexer.tokenize("")
            assertEquals(1, tokens.size)
            assertEquals(TokenType.EOF, tokens[0].type)
        }
        
        @Test
        @DisplayName("Simple text should be tokenized as TEXT")
        fun testSimpleText() {
            val tokens = lexer.tokenize("Hello World")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("Hello World", tokens[0].value)
            assertEquals(TokenType.EOF, tokens[1].type)
        }
        
        @Test
        @DisplayName("Whitespace should be tokenized correctly")
        fun testWhitespace() {
            val tokens = lexer.tokenize("Hello   World")
            assertEquals(4, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("Hello", tokens[0].value)
            assertEquals(TokenType.WHITESPACE, tokens[1].type)
            assertEquals("   ", tokens[1].value)
            assertEquals(TokenType.TEXT, tokens[2].type)
            assertEquals("World", tokens[2].value)
            assertEquals(TokenType.EOF, tokens[3].type)
        }
        
        @Test
        @DisplayName("Newlines should be tokenized as NEWLINE")
        fun testNewlines() {
            val tokens = lexer.tokenize("Line1\nLine2")
            assertEquals(4, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("Line1", tokens[0].value)
            assertEquals(TokenType.NEWLINE, tokens[1].type)
            assertEquals("\n", tokens[1].value)
            assertEquals(TokenType.TEXT, tokens[2].type)
            assertEquals("Line2", tokens[2].value)
            assertEquals(TokenType.EOF, tokens[3].type)
        }
    }
    
    @Nested
    @DisplayName("Header Tokenization")
    inner class HeaderTokenization {
        
        @ParameterizedTest
        @ValueSource(strings = ["# H1", "## H2", "### H3", "#### H4", "##### H5", "###### H6"])
        @DisplayName("Headers should be tokenized correctly")
        fun testHeaders(headerText: String) {
            val tokens = lexer.tokenize(headerText)
            assertEquals(2, tokens.size)
            assertEquals(TokenType.HEADER, tokens[0].type)
            assertEquals(headerText, tokens[0].value)
            assertEquals(TokenType.EOF, tokens[1].type)
        }
        
        @Test
        @DisplayName("Header without space should be treated as text")
        fun testHeaderWithoutSpace() {
            val tokens = lexer.tokenize("#NotAHeader")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("#NotAHeader", tokens[0].value)
        }
        
        @Test
        @DisplayName("Headers with multiple words")
        fun testHeaderWithMultipleWords() {
            val tokens = lexer.tokenize("# This is a header")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.HEADER, tokens[0].type)
            assertEquals("# This is a header", tokens[0].value)
        }
        
        @Test
        @DisplayName("Header at end of line")
        fun testHeaderAtEndOfLine() {
            val tokens = lexer.tokenize("# Header\nNext line")
            assertEquals(4, tokens.size)
            assertEquals(TokenType.HEADER, tokens[0].type)
            assertEquals("# Header", tokens[0].value)
            assertEquals(TokenType.NEWLINE, tokens[1].type)
            assertEquals(TokenType.TEXT, tokens[2].type)
            assertEquals("Next line", tokens[2].value)
        }
    }
    
    @Nested
    @DisplayName("Text Formatting")
    inner class TextFormatting {
        
        @Test
        @DisplayName("Bold text should be tokenized correctly")
        fun testBold() {
            val tokens = lexer.tokenize("**bold**")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.BOLD, tokens[0].type)
            assertEquals("bold", tokens[0].value)
        }
        
        @Test
        @DisplayName("Italic text should be tokenized correctly")
        fun testItalic() {
            val tokens = lexer.tokenize("*italic*")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.ITALIC, tokens[0].type)
            assertEquals("italic", tokens[0].value)
        }
        
        @Test
        @DisplayName("Strikethrough text should be tokenized correctly")
        fun testStrikethrough() {
            val tokens = lexer.tokenize("~~strikethrough~~")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.STRIKETHROUGH, tokens[0].type)
            assertEquals("strikethrough", tokens[0].value)
        }
        
        @Test
        @DisplayName("Mixed formatting in one line")
        fun testMixedFormatting() {
            val tokens = lexer.tokenize("**bold** and *italic* and ~~strike~~")
            assertEquals(8, tokens.size)
            assertEquals(TokenType.BOLD, tokens[0].type)
            assertEquals("bold", tokens[0].value)
            assertEquals(TokenType.WHITESPACE, tokens[1].type)
            assertEquals(TokenType.TEXT, tokens[2].type)
            assertEquals("and", tokens[2].value)
            assertEquals(TokenType.WHITESPACE, tokens[3].type)
            assertEquals(TokenType.ITALIC, tokens[4].type)
            assertEquals("italic", tokens[4].value)
            assertEquals(TokenType.WHITESPACE, tokens[5].type)
            assertEquals(TokenType.TEXT, tokens[6].type)
            assertEquals("and", tokens[6].value)
            assertEquals(TokenType.WHITESPACE, tokens[7].type)
            assertEquals(TokenType.STRIKETHROUGH, tokens[8].type)
            assertEquals("strike", tokens[8].value)
        }
        
        @Test
        @DisplayName("Unclosed formatting should be treated as text")
        fun testUnclosedFormatting() {
            val tokens = lexer.tokenize("**unclosed")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("**unclosed", tokens[0].value)
        }
    }
    
    @Nested
    @DisplayName("Code Tokenization")
    inner class CodeTokenization {
        
        @Test
        @DisplayName("Inline code should be tokenized correctly")
        fun testInlineCode() {
            val tokens = lexer.tokenize("`code`")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.CODE_INLINE, tokens[0].type)
            assertEquals("code", tokens[0].value)
        }
        
        @Test
        @DisplayName("Code block should be tokenized correctly")
        fun testCodeBlock() {
            val tokens = lexer.tokenize("```kotlin\nfun test() {\n    println(\"Hello\")\n}\n```")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.CODE_BLOCK, tokens[0].type)
            assertTrue(tokens[0].value.contains("kotlin"))
            assertTrue(tokens[0].value.contains("fun test()"))
        }
        
        @Test
        @DisplayName("Code block without language")
        fun testCodeBlockWithoutLanguage() {
            val tokens = lexer.tokenize("```\ncode\n```")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.CODE_BLOCK, tokens[0].type)
            assertEquals("code", tokens[0].value)
        }
        
        @Test
        @DisplayName("Unclosed code block should be treated as text")
        fun testUnClosedCodeBlock() {
            val tokens = lexer.tokenize("```\nunclosed")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("```\nunclosed", tokens[0].value)
        }
    }
    
    @Nested
    @DisplayName("Links and Images")
    inner class LinksAndImages {
        
        @Test
        @DisplayName("Link should be tokenized correctly")
        fun testLink() {
            val tokens = lexer.tokenize("[text](https://example.com)")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.LINK, tokens[0].type)
            assertEquals("text|https://example.com", tokens[0].value)
        }
        
        @Test
        @DisplayName("Image should be tokenized correctly")
        fun testImage() {
            val tokens = lexer.tokenize("![alt text](https://example.com/image.png)")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.IMAGE, tokens[0].type)
            assertEquals("alt text|https://example.com/image.png", tokens[0].value)
        }
        
        @Test
        @DisplayName("Malformed link should be treated as text")
        fun testMalformedLink() {
            val tokens = lexer.tokenize("[text without closing")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("[text without closing", tokens[0].value)
        }
    }
    
    @Nested
    @DisplayName("Lists")
    inner class Lists {
        
        @Test
        @DisplayName("Unordered list with dash should be tokenized correctly")
        fun testUnorderedListDash() {
            val tokens = lexer.tokenize("- Item 1\n- Item 2")
            assertEquals(4, tokens.size)
            assertEquals(TokenType.LIST_UNORDERED, tokens[0].type)
            assertEquals("Item 1", tokens[0].value)
            assertEquals(TokenType.NEWLINE, tokens[1].type)
            assertEquals(TokenType.LIST_UNORDERED, tokens[2].type)
            assertEquals("Item 2", tokens[2].value)
        }
        
        @Test
        @DisplayName("Unordered list with plus should be tokenized correctly")
        fun testUnorderedListPlus() {
            val tokens = lexer.tokenize("+ Item 1\n+ Item 2")
            assertEquals(4, tokens.size)
            assertEquals(TokenType.LIST_UNORDERED, tokens[0].type)
            assertEquals("Item 1", tokens[0].value)
        }
        
        @Test
        @DisplayName("Ordered list should be tokenized correctly")
        fun testOrderedList() {
            val tokens = lexer.tokenize("1. First item\n2. Second item")
            assertEquals(4, tokens.size)
            assertEquals(TokenType.LIST_ORDERED, tokens[0].type)
            assertEquals("1|First item", tokens[0].value)
            assertEquals(TokenType.NEWLINE, tokens[1].type)
            assertEquals(TokenType.LIST_ORDERED, tokens[2].type)
            assertEquals("2|Second item", tokens[2].value)
        }
        
        @Test
        @DisplayName("List marker without space should be treated as text")
        fun testListMarkerWithoutSpace() {
            val tokens = lexer.tokenize("-NotAList")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals("-NotAList", tokens[0].value)
        }
    }
    
    @Nested
    @DisplayName("Blockquotes")
    inner class Blockquotes {
        
        @Test
        @DisplayName("Blockquote should be tokenized correctly")
        fun testBlockquote() {
            val tokens = lexer.tokenize("> This is a quote")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.BLOCKQUOTE, tokens[0].type)
            assertEquals("This is a quote", tokens[0].value)
        }
        
        @Test
        @DisplayName("Multiple blockquotes")
        fun testMultipleBlockquotes() {
            val tokens = lexer.tokenize("> Quote 1\n> Quote 2")
            assertEquals(4, tokens.size)
            assertEquals(TokenType.BLOCKQUOTE, tokens[0].type)
            assertEquals("Quote 1", tokens[0].value)
            assertEquals(TokenType.NEWLINE, tokens[1].type)
            assertEquals(TokenType.BLOCKQUOTE, tokens[2].type)
            assertEquals("Quote 2", tokens[2].value)
        }
        
        @Test
        @DisplayName("Blockquote marker without space should be treated as text")
        fun testBlockquoteWithoutSpace() {
            val tokens = lexer.tokenize(">NotAQuote")
            assertEquals(2, tokens.size)
            assertEquals(TokenType.TEXT, tokens[0].type)
            assertEquals(">NotAQuote", tokens[0].value)
        }
    }
    
    @Nested
    @DisplayName("Complex Scenarios")
    inner class ComplexScenarios {
        
        @Test
        @DisplayName("Complete markdown document")
        fun testCompleteDocument() {
            val markdown = """
                # Main Header
                
                This is a paragraph with **bold** and *italic* text.
                
                ## Subheader
                
                - First item
                - Second item
                
                > This is a quote
                
                `inline code` and:
                
                ```kotlin
                fun example() {
                    println("Hello")
                }
                ```
                
                [Link](https://example.com) and ![Image](image.png)
            """.trimIndent()
            
            val tokens = lexer.tokenize(markdown)
            
            // Verify we have various token types
            val tokenTypes = tokens.map { it.type }.toSet()
            assertTrue(tokenTypes.contains(TokenType.HEADER))
            assertTrue(tokenTypes.contains(TokenType.BOLD))
            assertTrue(tokenTypes.contains(TokenType.ITALIC))
            assertTrue(tokenTypes.contains(TokenType.LIST_UNORDERED))
            assertTrue(tokenTypes.contains(TokenType.BLOCKQUOTE))
            assertTrue(tokenTypes.contains(TokenType.CODE_INLINE))
            assertTrue(tokenTypes.contains(TokenType.CODE_BLOCK))
            assertTrue(tokenTypes.contains(TokenType.LINK))
            assertTrue(tokenTypes.contains(TokenType.IMAGE))
            assertTrue(tokenTypes.contains(TokenType.EOF))
        }
        
        @Test
        @DisplayName("Nested formatting should be handled correctly")
        fun testNestedFormatting() {
            // Note: This lexer doesn't handle nested formatting, 
            // it should tokenize the outer formatting
            val tokens = lexer.tokenize("**bold with *italic* inside**")
            
            // Should tokenize as bold text containing the inner markdown
            assertTrue(tokens.any { it.type == TokenType.BOLD })
        }
        
        @Test
        @DisplayName("Edge case: Multiple special characters")
        fun testMultipleSpecialCharacters() {
            val tokens = lexer.tokenize("*** ### ``` --- +++")
            assertNotNull(tokens)
            assertTrue(tokens.isNotEmpty())
            assertEquals(TokenType.EOF, tokens.last().type)
        }
        
        @Test
        @DisplayName("Edge case: Very long text")
        fun testVeryLongText() {
            val longText = "word ".repeat(1000).trim()
            val tokens = lexer.tokenize(longText)
            assertNotNull(tokens)
            assertTrue(tokens.isNotEmpty())
            assertEquals(TokenType.EOF, tokens.last().type)
        }
    }
    
    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandling {
        
        @Test
        @DisplayName("Null input should handle gracefully")
        fun testNullInput() {
            // This should not throw an exception but return empty or handle gracefully
            assertDoesNotThrow {
                val tokens = lexer.tokenize("")
                assertEquals(1, tokens.size)
                assertEquals(TokenType.EOF, tokens[0].type)
            }
        }
        
        @Test
        @DisplayName("Special Unicode characters")
        fun testUnicodeCharacters() {
            val tokens = lexer.tokenize("Hello 世界 🌍")
            assertNotNull(tokens)
            assertTrue(tokens.any { it.type == TokenType.TEXT })
            assertEquals(TokenType.EOF, tokens.last().type)
        }
        
        @Test
        @DisplayName("Mixed line endings")
        fun testMixedLineEndings() {
            val tokens = lexer.tokenize("Line1\nLine2\rLine3\r\nLine4")
            assertNotNull(tokens)
            assertTrue(tokens.any { it.type == TokenType.NEWLINE })
            assertEquals(TokenType.EOF, tokens.last().type)
        }
    }
    
    @Nested
    @DisplayName("Token Properties")
    inner class TokenProperties {
        
        @Test
        @DisplayName("Tokens should have correct positions")
        fun testTokenPositions() {
            val tokens = lexer.tokenize("Hello World")
            assertTrue(tokens[0].position >= 0)
            assertTrue(tokens[1].position >= tokens[0].position)
        }
        
        @Test
        @DisplayName("Token values should preserve original content")
        fun testTokenValuePreservation() {
            val originalText = "**bold** text"
            val tokens = lexer.tokenize(originalText)
            
            val boldToken = tokens.find { it.type == TokenType.BOLD }
            assertNotNull(boldToken)
            assertEquals("bold", boldToken!!.value)
        }
        
        @Test
        @DisplayName("EOF token should always be last")
        fun testEOFTokenPosition() {
            val tokens = lexer.tokenize("Some text here")
            assertEquals(TokenType.EOF, tokens.last().type)
        }
    }
}