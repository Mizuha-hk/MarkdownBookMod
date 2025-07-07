package com.markdownbookmod.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("MarkdownLexer Tests")
class MarkdownLexerTest {
    
    private lateinit var lexer: MarkdownLexer
    
    @BeforeEach
    fun setUp() {
        lexer = MarkdownLexer()
    }
    
    @Nested
    @DisplayName("Header Tokenization")
    inner class HeaderTokenization {
        
        @Test
        @DisplayName("Should tokenize H1 header")
        fun shouldTokenizeH1Header() {
            val tokens = lexer.tokenize("# Header 1")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.HEADER, tokens[0].type)
            assertEquals("# Header 1", tokens[0].content)
            assertEquals(MarkdownLexer.TokenType.EOF, tokens[1].type)
        }
        
        @Test
        @DisplayName("Should tokenize H2 header")
        fun shouldTokenizeH2Header() {
            val tokens = lexer.tokenize("## Header 2")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.HEADER, tokens[0].type)
            assertEquals("## Header 2", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize H3 header")
        fun shouldTokenizeH3Header() {
            val tokens = lexer.tokenize("### Header 3")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.HEADER, tokens[0].type)
            assertEquals("### Header 3", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize multiple headers")
        fun shouldTokenizeMultipleHeaders() {
            val tokens = lexer.tokenize("# H1\n## H2\n### H3")
            
            assertEquals(6, tokens.size) // 3 headers + 2 line breaks + EOF
            assertEquals(MarkdownLexer.TokenType.HEADER, tokens[0].type)
            assertEquals("# H1", tokens[0].content)
            assertEquals(MarkdownLexer.TokenType.LINE_BREAK, tokens[1].type)
            assertEquals(MarkdownLexer.TokenType.HEADER, tokens[2].type)
            assertEquals("## H2", tokens[2].content)
        }
    }
    
    @Nested
    @DisplayName("Text Formatting Tokenization")
    inner class TextFormattingTokenization {
        
        @Test
        @DisplayName("Should tokenize bold text with **")
        fun shouldTokenizeBoldTextWithDoubleAsterisks() {
            val tokens = lexer.tokenize("**bold text**")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.BOLD, tokens[0].type)
            assertEquals("**bold text**", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize bold text with __")
        fun shouldTokenizeBoldTextWithDoubleUnderscores() {
            val tokens = lexer.tokenize("__bold text__")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.BOLD, tokens[0].type)
            assertEquals("__bold text__", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize italic text with *")
        fun shouldTokenizeItalicTextWithAsterisk() {
            val tokens = lexer.tokenize("*italic text*")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.ITALIC, tokens[0].type)
            assertEquals("*italic text*", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize italic text with _")
        fun shouldTokenizeItalicTextWithUnderscore() {
            val tokens = lexer.tokenize("_italic text_")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.ITALIC, tokens[0].type)
            assertEquals("_italic text_", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize strikethrough text")
        fun shouldTokenizeStrikethroughText() {
            val tokens = lexer.tokenize("~~strikethrough~~")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.STRIKETHROUGH, tokens[0].type)
            assertEquals("~~strikethrough~~", tokens[0].content)
        }
    }
    
    @Nested
    @DisplayName("Code Tokenization")
    inner class CodeTokenization {
        
        @Test
        @DisplayName("Should tokenize inline code")
        fun shouldTokenizeInlineCode() {
            val tokens = lexer.tokenize("`inline code`")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.CODE_INLINE, tokens[0].type)
            assertEquals("`inline code`", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize code block")
        fun shouldTokenizeCodeBlock() {
            val tokens = lexer.tokenize("```code block```")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.CODE_BLOCK, tokens[0].type)
            assertEquals("```code block```", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize multiline code block")
        fun shouldTokenizeMultilineCodeBlock() {
            val codeBlock = """```
                |fun test() {
                |    println("Hello")
                |}
                |```""".trimMargin()
            
            val tokens = lexer.tokenize(codeBlock)
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.CODE_BLOCK, tokens[0].type)
            assertTrue(tokens[0].content.contains("fun test()"))
        }
    }
    
    @Nested
    @DisplayName("Link and Image Tokenization")
    inner class LinkAndImageTokenization {
        
        @Test
        @DisplayName("Should tokenize link")
        fun shouldTokenizeLink() {
            val tokens = lexer.tokenize("[link text](https://example.com)")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.LINK, tokens[0].type)
            assertEquals("[link text](https://example.com)", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize image")
        fun shouldTokenizeImage() {
            val tokens = lexer.tokenize("![alt text](image.png)")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.IMAGE, tokens[0].type)
            assertEquals("![alt text](image.png)", tokens[0].content)
        }
    }
    
    @Nested
    @DisplayName("List Tokenization")
    inner class ListTokenization {
        
        @Test
        @DisplayName("Should tokenize list item with -")
        fun shouldTokenizeListItemWithHyphen() {
            val tokens = lexer.tokenize("- List item")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.LIST_ITEM, tokens[0].type)
            assertEquals("- List item", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize list item with +")
        fun shouldTokenizeListItemWithPlus() {
            val tokens = lexer.tokenize("+ List item")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.LIST_ITEM, tokens[0].type)
            assertEquals("+ List item", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize multiple list items")
        fun shouldTokenizeMultipleListItems() {
            val tokens = lexer.tokenize("- Item 1\n- Item 2\n- Item 3")
            
            assertEquals(6, tokens.size) // 3 items + 2 line breaks + EOF
            assertEquals(MarkdownLexer.TokenType.LIST_ITEM, tokens[0].type)
            assertEquals("- Item 1", tokens[0].content)
            assertEquals(MarkdownLexer.TokenType.LINE_BREAK, tokens[1].type)
            assertEquals(MarkdownLexer.TokenType.LIST_ITEM, tokens[2].type)
            assertEquals("- Item 2", tokens[2].content)
        }
    }
    
    @Nested
    @DisplayName("Blockquote Tokenization")
    inner class BlockquoteTokenization {
        
        @Test
        @DisplayName("Should tokenize blockquote")
        fun shouldTokenizeBlockquote() {
            val tokens = lexer.tokenize("> This is a quote")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.BLOCKQUOTE, tokens[0].type)
            assertEquals("> This is a quote", tokens[0].content)
        }
        
        @Test
        @DisplayName("Should tokenize blockquote without space")
        fun shouldTokenizeBlockquoteWithoutSpace() {
            val tokens = lexer.tokenize(">Quote without space")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.BLOCKQUOTE, tokens[0].type)
            assertEquals("> Quote without space", tokens[0].content)
        }
    }
    
    @Nested
    @DisplayName("Mixed Content Tokenization")
    inner class MixedContentTokenization {
        
        @Test
        @DisplayName("Should tokenize mixed content")
        fun shouldTokenizeMixedContent() {
            val markdown = """# Header
                |This is **bold** and *italic* text.
                |> Blockquote
                |- List item
                |`inline code`""".trimMargin()
            
            val tokens = lexer.tokenize(markdown)
            
            assertTrue(tokens.size > 5)
            assertEquals(MarkdownLexer.TokenType.HEADER, tokens[0].type)
            assertTrue(tokens.any { it.type == MarkdownLexer.TokenType.BOLD })
            assertTrue(tokens.any { it.type == MarkdownLexer.TokenType.ITALIC })
            assertTrue(tokens.any { it.type == MarkdownLexer.TokenType.BLOCKQUOTE })
            assertTrue(tokens.any { it.type == MarkdownLexer.TokenType.LIST_ITEM })
            assertTrue(tokens.any { it.type == MarkdownLexer.TokenType.CODE_INLINE })
        }
        
        @Test
        @DisplayName("Should handle empty input")
        fun shouldHandleEmptyInput() {
            val tokens = lexer.tokenize("")
            
            assertEquals(1, tokens.size)
            assertEquals(MarkdownLexer.TokenType.EOF, tokens[0].type)
        }
        
        @Test
        @DisplayName("Should handle plain text")
        fun shouldHandlePlainText() {
            val tokens = lexer.tokenize("Just plain text")
            
            assertEquals(2, tokens.size)
            assertEquals(MarkdownLexer.TokenType.TEXT, tokens[0].type)
            assertEquals("Just plain text", tokens[0].content)
            assertEquals(MarkdownLexer.TokenType.EOF, tokens[1].type)
        }
    }
    
    @Nested
    @DisplayName("Position Tracking")
    inner class PositionTracking {
        
        @Test
        @DisplayName("Should track token positions")
        fun shouldTrackTokenPositions() {
            val tokens = lexer.tokenize("# Header\nText")
            
            assertEquals(0, tokens[0].position) // Header starts at 0
            assertEquals(9, tokens[1].position) // Line break at position 9
            assertEquals(10, tokens[2].position) // Text starts at position 10
        }
    }
    
    @Nested
    @DisplayName("Next Token Functionality")
    inner class NextTokenFunctionality {
        
        @Test
        @DisplayName("Should get next token correctly")
        fun shouldGetNextTokenCorrectly() {
            lexer.initialize("# Header")
            
            val token1 = lexer.nextToken()
            assertEquals(MarkdownLexer.TokenType.HEADER, token1.type)
            assertEquals("# Header", token1.content)
            
            val token2 = lexer.nextToken()
            assertEquals(MarkdownLexer.TokenType.EOF, token2.type)
        }
        
        @Test
        @DisplayName("Should handle sequential token reading")
        fun shouldHandleSequentialTokenReading() {
            lexer.initialize("**bold** *italic*")
            
            val token1 = lexer.nextToken()
            assertEquals(MarkdownLexer.TokenType.BOLD, token1.type)
            
            val token2 = lexer.nextToken()
            assertEquals(MarkdownLexer.TokenType.TEXT, token2.type)
            assertEquals(" ", token2.content)
            
            val token3 = lexer.nextToken()
            assertEquals(MarkdownLexer.TokenType.ITALIC, token3.type)
        }
    }
}