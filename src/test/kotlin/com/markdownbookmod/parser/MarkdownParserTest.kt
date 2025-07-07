package com.markdownbookmod.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertIs

@DisplayName("MarkdownParser Tests")
class MarkdownParserTest {
    
    private lateinit var parser: MarkdownParser
    private lateinit var lexer: MarkdownLexer
    
    @BeforeEach
    fun setUp() {
        parser = MarkdownParser()
        lexer = MarkdownLexer()
    }
    
    private fun parseMarkdown(markdown: String): MarkdownParser.ASTNode.Document {
        val tokens = lexer.tokenize(markdown)
        return parser.parse(tokens)
    }
    
    @Nested
    @DisplayName("Document Parsing")
    inner class DocumentParsing {
        
        @Test
        @DisplayName("Should parse empty document")
        fun shouldParseEmptyDocument() {
            val document = parseMarkdown("")
            
            assertIs<MarkdownParser.ASTNode.Document>(document)
            assertTrue(document.children.isEmpty())
        }
        
        @Test
        @DisplayName("Should parse document with single element")
        fun shouldParseDocumentWithSingleElement() {
            val document = parseMarkdown("# Header")
            
            assertEquals(1, document.children.size)
            assertIs<MarkdownParser.ASTNode.Header>(document.children[0])
        }
        
        @Test
        @DisplayName("Should parse document with multiple elements")
        fun shouldParseDocumentWithMultipleElements() {
            val markdown = """# Header
                |This is text.
                |> Quote""".trimMargin()
            
            val document = parseMarkdown(markdown)
            
            assertEquals(5, document.children.size) // header + line break + paragraph + line break + blockquote
            assertIs<MarkdownParser.ASTNode.Header>(document.children[0])
            assertIs<MarkdownParser.ASTNode.LineBreak>(document.children[1])
            assertIs<MarkdownParser.ASTNode.Paragraph>(document.children[2])
        }
    }
    
    @Nested
    @DisplayName("Header Parsing")
    inner class HeaderParsing {
        
        @Test
        @DisplayName("Should parse H1 header")
        fun shouldParseH1Header() {
            val document = parseMarkdown("# Header 1")
            
            val header = document.children[0] as MarkdownParser.ASTNode.Header
            assertEquals(1, header.level)
            assertEquals("Header 1", header.text)
        }
        
        @Test
        @DisplayName("Should parse H2 header")
        fun shouldParseH2Header() {
            val document = parseMarkdown("## Header 2")
            
            val header = document.children[0] as MarkdownParser.ASTNode.Header
            assertEquals(2, header.level)
            assertEquals("Header 2", header.text)
        }
        
        @Test
        @DisplayName("Should parse H6 header")
        fun shouldParseH6Header() {
            val document = parseMarkdown("###### Header 6")
            
            val header = document.children[0] as MarkdownParser.ASTNode.Header
            assertEquals(6, header.level)
            assertEquals("Header 6", header.text)
        }
        
        @Test
        @DisplayName("Should parse header with special characters")
        fun shouldParseHeaderWithSpecialCharacters() {
            val document = parseMarkdown("# Header with *special* characters!")
            
            val header = document.children[0] as MarkdownParser.ASTNode.Header
            assertEquals(1, header.level)
            assertEquals("Header with *special* characters!", header.text)
        }
    }
    
    @Nested
    @DisplayName("Paragraph Parsing")
    inner class ParagraphParsing {
        
        @Test
        @DisplayName("Should parse simple paragraph")
        fun shouldParseSimpleParagraph() {
            val document = parseMarkdown("This is a simple paragraph.")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            assertEquals(1, paragraph.children.size)
            
            val text = paragraph.children[0] as MarkdownParser.ASTNode.Text
            assertEquals("This is a simple paragraph.", text.content)
        }
        
        @Test
        @DisplayName("Should parse paragraph with inline formatting")
        fun shouldParseParagraphWithInlineFormatting() {
            val document = parseMarkdown("This is **bold** and *italic* text.")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            assertEquals(5, paragraph.children.size)
            
            assertIs<MarkdownParser.ASTNode.Text>(paragraph.children[0])
            assertIs<MarkdownParser.ASTNode.Bold>(paragraph.children[1])
            assertIs<MarkdownParser.ASTNode.Text>(paragraph.children[2])
            assertIs<MarkdownParser.ASTNode.Italic>(paragraph.children[3])
            assertIs<MarkdownParser.ASTNode.Text>(paragraph.children[4])
        }
        
        @Test
        @DisplayName("Should parse paragraph with links")
        fun shouldParseParagraphWithLinks() {
            val document = parseMarkdown("Check out [this link](https://example.com) for more info.")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownParser.ASTNode.Link })
            
            val link = paragraph.children.first { it is MarkdownParser.ASTNode.Link } as MarkdownParser.ASTNode.Link
            assertEquals("this link", link.text)
            assertEquals("https://example.com", link.url)
        }
    }
    
    @Nested
    @DisplayName("Text Formatting Parsing")
    inner class TextFormattingParsing {
        
        @Test
        @DisplayName("Should parse bold text with **")
        fun shouldParseBoldTextWithDoubleAsterisks() {
            val document = parseMarkdown("**bold text**")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val bold = paragraph.children[0] as MarkdownParser.ASTNode.Bold
            assertEquals("bold text", bold.text)
        }
        
        @Test
        @DisplayName("Should parse bold text with __")
        fun shouldParseBoldTextWithDoubleUnderscores() {
            val document = parseMarkdown("__bold text__")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val bold = paragraph.children[0] as MarkdownParser.ASTNode.Bold
            assertEquals("bold text", bold.text)
        }
        
        @Test
        @DisplayName("Should parse italic text with *")
        fun shouldParseItalicTextWithAsterisk() {
            val document = parseMarkdown("*italic text*")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val italic = paragraph.children[0] as MarkdownParser.ASTNode.Italic
            assertEquals("italic text", italic.text)
        }
        
        @Test
        @DisplayName("Should parse italic text with _")
        fun shouldParseItalicTextWithUnderscore() {
            val document = parseMarkdown("_italic text_")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val italic = paragraph.children[0] as MarkdownParser.ASTNode.Italic
            assertEquals("italic text", italic.text)
        }
        
        @Test
        @DisplayName("Should parse strikethrough text")
        fun shouldParseStrikethroughText() {
            val document = parseMarkdown("~~strikethrough text~~")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val strikethrough = paragraph.children[0] as MarkdownParser.ASTNode.Strikethrough
            assertEquals("strikethrough text", strikethrough.text)
        }
        
        @Test
        @DisplayName("Should parse nested formatting")
        fun shouldParseNestedFormatting() {
            val document = parseMarkdown("**bold *and italic* text**")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            assertTrue(paragraph.children.isNotEmpty())
            assertIs<MarkdownParser.ASTNode.Bold>(paragraph.children[0])
        }
    }
    
    @Nested
    @DisplayName("Code Parsing")
    inner class CodeParsing {
        
        @Test
        @DisplayName("Should parse inline code")
        fun shouldParseInlineCode() {
            val document = parseMarkdown("`inline code`")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val code = paragraph.children[0] as MarkdownParser.ASTNode.InlineCode
            assertEquals("inline code", code.text)
        }
        
        @Test
        @DisplayName("Should parse code block")
        fun shouldParseCodeBlock() {
            val document = parseMarkdown("```\ncode block\n```")
            
            val codeBlock = document.children[0] as MarkdownParser.ASTNode.CodeBlock
            assertTrue(codeBlock.text.contains("code block"))
        }
        
        @Test
        @DisplayName("Should parse code block with language")
        fun shouldParseCodeBlockWithLanguage() {
            val codeBlock = """```kotlin
                |fun test() {
                |    println("Hello")
                |}
                |```""".trimMargin()
            
            val document = parseMarkdown(codeBlock)
            
            val code = document.children[0] as MarkdownParser.ASTNode.CodeBlock
            assertEquals("kotlin", code.language)
            assertTrue(code.text.contains("fun test()"))
        }
        
        @Test
        @DisplayName("Should parse multiline code block")
        fun shouldParseMultilineCodeBlock() {
            val codeBlock = """```
                |line 1
                |line 2
                |line 3
                |```""".trimMargin()
            
            val document = parseMarkdown(codeBlock)
            
            val code = document.children[0] as MarkdownParser.ASTNode.CodeBlock
            assertTrue(code.text.contains("line 1"))
            assertTrue(code.text.contains("line 2"))
            assertTrue(code.text.contains("line 3"))
        }
    }
    
    @Nested
    @DisplayName("Link and Image Parsing")
    inner class LinkAndImageParsing {
        
        @Test
        @DisplayName("Should parse simple link")
        fun shouldParseSimpleLink() {
            val document = parseMarkdown("[link text](https://example.com)")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val link = paragraph.children[0] as MarkdownParser.ASTNode.Link
            assertEquals("link text", link.text)
            assertEquals("https://example.com", link.url)
        }
        
        @Test
        @DisplayName("Should parse link with title")
        fun shouldParseLinkWithTitle() {
            val document = parseMarkdown("[GitHub](https://github.com)")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val link = paragraph.children[0] as MarkdownParser.ASTNode.Link
            assertEquals("GitHub", link.text)
            assertEquals("https://github.com", link.url)
        }
        
        @Test
        @DisplayName("Should parse image")
        fun shouldParseImage() {
            val document = parseMarkdown("![alt text](image.png)")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val image = paragraph.children[0] as MarkdownParser.ASTNode.Image
            assertEquals("alt text", image.altText)
            assertEquals("image.png", image.url)
        }
        
        @Test
        @DisplayName("Should parse image with empty alt text")
        fun shouldParseImageWithEmptyAltText() {
            val document = parseMarkdown("![](image.png)")
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val image = paragraph.children[0] as MarkdownParser.ASTNode.Image
            assertEquals("", image.altText)
            assertEquals("image.png", image.url)
        }
    }
    
    @Nested
    @DisplayName("List Parsing")
    inner class ListParsing {
        
        @Test
        @DisplayName("Should parse single list item")
        fun shouldParseSingleListItem() {
            val document = parseMarkdown("- List item")
            
            val list = document.children[0] as MarkdownParser.ASTNode.UnorderedList
            assertEquals(1, list.items.size)
            assertEquals("List item", list.items[0].text)
        }
        
        @Test
        @DisplayName("Should parse multiple list items")
        fun shouldParseMultipleListItems() {
            val markdown = """- Item 1
                |- Item 2
                |- Item 3""".trimMargin()
            
            val document = parseMarkdown(markdown)
            
            val list = document.children[0] as MarkdownParser.ASTNode.UnorderedList
            assertEquals(3, list.items.size)
            assertEquals("Item 1", list.items[0].text)
            assertEquals("Item 2", list.items[1].text)
            assertEquals("Item 3", list.items[2].text)
        }
        
        @Test
        @DisplayName("Should parse list with + marker")
        fun shouldParseListWithPlusMarker() {
            val markdown = """+ Item 1
                |+ Item 2""".trimMargin()
            
            val document = parseMarkdown(markdown)
            
            val list = document.children[0] as MarkdownParser.ASTNode.UnorderedList
            assertEquals(2, list.items.size)
            assertEquals("Item 1", list.items[0].text)
            assertEquals("Item 2", list.items[1].text)
        }
    }
    
    @Nested
    @DisplayName("Blockquote Parsing")
    inner class BlockquoteParsing {
        
        @Test
        @DisplayName("Should parse simple blockquote")
        fun shouldParseSimpleBlockquote() {
            val document = parseMarkdown("> This is a quote")
            
            val blockquote = document.children[0] as MarkdownParser.ASTNode.Blockquote
            assertEquals("This is a quote", blockquote.text)
        }
        
        @Test
        @DisplayName("Should parse blockquote without space")
        fun shouldParseBlockquoteWithoutSpace() {
            val document = parseMarkdown(">Quote without space")
            
            val blockquote = document.children[0] as MarkdownParser.ASTNode.Blockquote
            assertEquals("Quote without space", blockquote.text)
        }
        
        @Test
        @DisplayName("Should parse multiline blockquote")
        fun shouldParseMultilineBlockquote() {
            val markdown = """> First line
                |> Second line""".trimMargin()
            
            val document = parseMarkdown(markdown)
            
            assertEquals(3, document.children.size) // blockquote + line break + blockquote
            assertIs<MarkdownParser.ASTNode.Blockquote>(document.children[0])
            assertIs<MarkdownParser.ASTNode.Blockquote>(document.children[2])
        }
    }
    
    @Nested
    @DisplayName("Complex Parsing Scenarios")
    inner class ComplexParsingScenarios {
        
        @Test
        @DisplayName("Should parse mixed content document")
        fun shouldParseMixedContentDocument() {
            val markdown = """# Header
                |This is **bold** text.
                |
                |> A quote
                |
                |- List item 1
                |- List item 2
                |
                |```
                |code block
                |```""".trimMargin()
            
            val document = parseMarkdown(markdown)
            
            assertTrue(document.children.isNotEmpty())
            assertTrue(document.children.any { it is MarkdownParser.ASTNode.Header })
            assertTrue(document.children.any { it is MarkdownParser.ASTNode.Paragraph })
            assertTrue(document.children.any { it is MarkdownParser.ASTNode.Blockquote })
            assertTrue(document.children.any { it is MarkdownParser.ASTNode.UnorderedList })
            assertTrue(document.children.any { it is MarkdownParser.ASTNode.CodeBlock })
        }
        
        @Test
        @DisplayName("Should handle malformed markdown gracefully")
        fun shouldHandleMalformedMarkdownGracefully() {
            val malformed = "**unclosed bold [incomplete link( >incomplete quote"
            
            val document = parseMarkdown(malformed)
            
            assertNotNull(document)
            assertTrue(document.children.isNotEmpty())
        }
        
        @Test
        @DisplayName("Should parse markdown with special characters")
        fun shouldParseMarkdownWithSpecialCharacters() {
            val markdown = "Text with & special < characters > and symbols @#$%"
            
            val document = parseMarkdown(markdown)
            
            val paragraph = document.children[0] as MarkdownParser.ASTNode.Paragraph
            val text = paragraph.children[0] as MarkdownParser.ASTNode.Text
            assertEquals(markdown, text.content)
        }
    }
    
    @Nested
    @DisplayName("Line Break Handling")
    inner class LineBreakHandling {
        
        @Test
        @DisplayName("Should parse single line break")
        fun shouldParseSingleLineBreak() {
            val document = parseMarkdown("Line 1\nLine 2")
            
            assertEquals(3, document.children.size)
            assertIs<MarkdownParser.ASTNode.Paragraph>(document.children[0])
            assertIs<MarkdownParser.ASTNode.LineBreak>(document.children[1])
            assertIs<MarkdownParser.ASTNode.Paragraph>(document.children[2])
        }
        
        @Test
        @DisplayName("Should parse multiple line breaks")
        fun shouldParseMultipleLineBreaks() {
            val document = parseMarkdown("Line 1\n\nLine 2")
            
            assertTrue(document.children.size >= 3)
            assertTrue(document.children.any { it is MarkdownParser.ASTNode.LineBreak })
        }
    }
}