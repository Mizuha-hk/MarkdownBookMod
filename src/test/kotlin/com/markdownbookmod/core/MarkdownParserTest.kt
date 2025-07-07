package com.markdownbookmod.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.Assertions.*

@DisplayName("MarkdownParser Tests")
class MarkdownParserTest {
    
    private lateinit var parser: MarkdownParser
    private lateinit var lexer: MarkdownLexer
    
    @BeforeEach
    fun setUp() {
        parser = MarkdownParser()
        lexer = MarkdownLexer()
    }
    
    private fun parseMarkdown(text: String): MarkdownNode.Document {
        val tokens = lexer.tokenize(text)
        return parser.parse(tokens)
    }
    
    @Nested
    @DisplayName("Basic Parsing")
    inner class BasicParsing {
        
        @Test
        @DisplayName("Empty input should create empty document")
        fun testEmptyInput() {
            val document = parseMarkdown("")
            assertEquals(0, document.children.size)
        }
        
        @Test
        @DisplayName("Simple text should create paragraph with text node")
        fun testSimpleText() {
            val document = parseMarkdown("Hello World")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertEquals(1, paragraph.children.size)
            assertTrue(paragraph.children[0] is MarkdownNode.Text)
            
            val text = paragraph.children[0] as MarkdownNode.Text
            assertEquals("Hello World", text.content)
        }
        
        @Test
        @DisplayName("Multiple paragraphs should be separated by line breaks")
        fun testMultipleParagraphs() {
            val document = parseMarkdown("Paragraph 1\n\nParagraph 2")
            assertTrue(document.children.size >= 2)
            
            // Should contain paragraphs and line breaks
            val paragraphs = document.children.filterIsInstance<MarkdownNode.Paragraph>()
            assertTrue(paragraphs.size >= 2)
        }
    }
    
    @Nested
    @DisplayName("Header Parsing")
    inner class HeaderParsing {
        
        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3, 4, 5, 6])
        @DisplayName("Headers of different levels should be parsed correctly")
        fun testHeaderLevels(level: Int) {
            val headerText = "#".repeat(level) + " Header text"
            val document = parseMarkdown(headerText)
            
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Header)
            
            val header = document.children[0] as MarkdownNode.Header
            assertEquals(level, header.level)
            assertEquals("Header text", header.text)
        }
        
        @Test
        @DisplayName("Header with complex text")
        fun testHeaderWithComplexText() {
            val document = parseMarkdown("## Introduction to Programming")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Header)
            
            val header = document.children[0] as MarkdownNode.Header
            assertEquals(2, header.level)
            assertEquals("Introduction to Programming", header.text)
        }
        
        @Test
        @DisplayName("Multiple headers")
        fun testMultipleHeaders() {
            val markdown = """
                # Main Title
                ## Subtitle
                ### Section
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            val headers = document.children.filterIsInstance<MarkdownNode.Header>()
            assertEquals(3, headers.size)
            
            assertEquals(1, headers[0].level)
            assertEquals("Main Title", headers[0].text)
            assertEquals(2, headers[1].level)
            assertEquals("Subtitle", headers[1].text)
            assertEquals(3, headers[2].level)
            assertEquals("Section", headers[2].text)
        }
    }
    
    @Nested
    @DisplayName("Text Formatting Parsing")
    inner class TextFormattingParsing {
        
        @Test
        @DisplayName("Bold text should be parsed correctly")
        fun testBoldText() {
            val document = parseMarkdown("This is **bold** text")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.Bold })
            
            val bold = paragraph.children.find { it is MarkdownNode.Bold } as MarkdownNode.Bold
            assertEquals("bold", bold.text)
        }
        
        @Test
        @DisplayName("Italic text should be parsed correctly")
        fun testItalicText() {
            val document = parseMarkdown("This is *italic* text")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.Italic })
            
            val italic = paragraph.children.find { it is MarkdownNode.Italic } as MarkdownNode.Italic
            assertEquals("italic", italic.text)
        }
        
        @Test
        @DisplayName("Strikethrough text should be parsed correctly")
        fun testStrikethroughText() {
            val document = parseMarkdown("This is ~~strikethrough~~ text")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.Strikethrough })
            
            val strikethrough = paragraph.children.find { it is MarkdownNode.Strikethrough } as MarkdownNode.Strikethrough
            assertEquals("strikethrough", strikethrough.text)
        }
        
        @Test
        @DisplayName("Mixed formatting in paragraph")
        fun testMixedFormatting() {
            val document = parseMarkdown("Text with **bold**, *italic*, and ~~strikethrough~~")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.Bold })
            assertTrue(paragraph.children.any { it is MarkdownNode.Italic })
            assertTrue(paragraph.children.any { it is MarkdownNode.Strikethrough })
        }
    }
    
    @Nested
    @DisplayName("Code Parsing")
    inner class CodeParsing {
        
        @Test
        @DisplayName("Inline code should be parsed correctly")
        fun testInlineCode() {
            val document = parseMarkdown("Use `console.log()` for debugging")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.CodeInline })
            
            val code = paragraph.children.find { it is MarkdownNode.CodeInline } as MarkdownNode.CodeInline
            assertEquals("console.log()", code.code)
        }
        
        @Test
        @DisplayName("Code block should be parsed correctly")
        fun testCodeBlock() {
            val codeText = """
                ```kotlin
                fun example() {
                    println("Hello")
                }
                ```
            """.trimIndent()
            
            val document = parseMarkdown(codeText)
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.CodeBlock)
            
            val codeBlock = document.children[0] as MarkdownNode.CodeBlock
            assertEquals("kotlin", codeBlock.language)
            assertTrue(codeBlock.code.contains("fun example()"))
        }
        
        @Test
        @DisplayName("Code block without language")
        fun testCodeBlockWithoutLanguage() {
            val codeText = """
                ```
                code without language
                ```
            """.trimIndent()
            
            val document = parseMarkdown(codeText)
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.CodeBlock)
            
            val codeBlock = document.children[0] as MarkdownNode.CodeBlock
            assertEquals("", codeBlock.language)
            assertEquals("code without language", codeBlock.code)
        }
    }
    
    @Nested
    @DisplayName("Links and Images")
    inner class LinksAndImages {
        
        @Test
        @DisplayName("Link should be parsed correctly")
        fun testLink() {
            val document = parseMarkdown("Check out [this link](https://example.com)")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.Link })
            
            val link = paragraph.children.find { it is MarkdownNode.Link } as MarkdownNode.Link
            assertEquals("this link", link.text)
            assertEquals("https://example.com", link.url)
        }
        
        @Test
        @DisplayName("Image should be parsed correctly")
        fun testImage() {
            val document = parseMarkdown("Here is an image: ![alt text](https://example.com/image.png)")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.Image })
            
            val image = paragraph.children.find { it is MarkdownNode.Image } as MarkdownNode.Image
            assertEquals("alt text", image.altText)
            assertEquals("https://example.com/image.png", image.url)
        }
        
        @Test
        @DisplayName("Multiple links in paragraph")
        fun testMultipleLinks() {
            val document = parseMarkdown("Visit [site1](url1) and [site2](url2)")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            val links = paragraph.children.filterIsInstance<MarkdownNode.Link>()
            assertEquals(2, links.size)
            
            assertEquals("site1", links[0].text)
            assertEquals("url1", links[0].url)
            assertEquals("site2", links[1].text)
            assertEquals("url2", links[1].url)
        }
    }
    
    @Nested
    @DisplayName("List Parsing")
    inner class ListParsing {
        
        @Test
        @DisplayName("Unordered list should be parsed correctly")
        fun testUnorderedList() {
            val markdown = """
                - Item 1
                - Item 2
                - Item 3
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            assertTrue(document.children.any { it is MarkdownNode.UnorderedList })
            
            val list = document.children.find { it is MarkdownNode.UnorderedList } as MarkdownNode.UnorderedList
            assertEquals(3, list.items.size)
            assertEquals("Item 1", list.items[0].content)
            assertEquals("Item 2", list.items[1].content)
            assertEquals("Item 3", list.items[2].content)
        }
        
        @Test
        @DisplayName("Ordered list should be parsed correctly")
        fun testOrderedList() {
            val markdown = """
                1. First item
                2. Second item
                3. Third item
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            assertTrue(document.children.any { it is MarkdownNode.OrderedList })
            
            val list = document.children.find { it is MarkdownNode.OrderedList } as MarkdownNode.OrderedList
            assertEquals(3, list.items.size)
            assertEquals("First item", list.items[0].content)
            assertEquals("Second item", list.items[1].content)
            assertEquals("Third item", list.items[2].content)
        }
        
        @Test
        @DisplayName("Mixed list types")
        fun testMixedListTypes() {
            val markdown = """
                - Unordered item
                
                1. Ordered item
                2. Another ordered item
                
                - Another unordered item
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            val unorderedLists = document.children.filterIsInstance<MarkdownNode.UnorderedList>()
            val orderedLists = document.children.filterIsInstance<MarkdownNode.OrderedList>()
            
            assertTrue(unorderedLists.isNotEmpty())
            assertTrue(orderedLists.isNotEmpty())
        }
        
        @Test
        @DisplayName("Single item lists")
        fun testSingleItemLists() {
            val unorderedDoc = parseMarkdown("- Single item")
            val orderedDoc = parseMarkdown("1. Single item")
            
            assertTrue(unorderedDoc.children.any { it is MarkdownNode.UnorderedList })
            assertTrue(orderedDoc.children.any { it is MarkdownNode.OrderedList })
            
            val unorderedList = unorderedDoc.children.find { it is MarkdownNode.UnorderedList } as MarkdownNode.UnorderedList
            val orderedList = orderedDoc.children.find { it is MarkdownNode.OrderedList } as MarkdownNode.OrderedList
            
            assertEquals(1, unorderedList.items.size)
            assertEquals(1, orderedList.items.size)
            assertEquals("Single item", unorderedList.items[0].content)
            assertEquals("Single item", orderedList.items[0].content)
        }
    }
    
    @Nested
    @DisplayName("Blockquote Parsing")
    inner class BlockquoteParsing {
        
        @Test
        @DisplayName("Single blockquote should be parsed correctly")
        fun testSingleBlockquote() {
            val document = parseMarkdown("> This is a quote")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Blockquote)
            
            val quote = document.children[0] as MarkdownNode.Blockquote
            assertEquals("This is a quote", quote.content)
        }
        
        @Test
        @DisplayName("Multiple blockquotes")
        fun testMultipleBlockquotes() {
            val markdown = """
                > First quote
                > Second quote
                > Third quote
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            val quotes = document.children.filterIsInstance<MarkdownNode.Blockquote>()
            assertEquals(3, quotes.size)
            
            assertEquals("First quote", quotes[0].content)
            assertEquals("Second quote", quotes[1].content)
            assertEquals("Third quote", quotes[2].content)
        }
        
        @Test
        @DisplayName("Blockquote with complex content")
        fun testBlockquoteWithComplexContent() {
            val document = parseMarkdown("> This quote has **bold** text in it")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Blockquote)
            
            val quote = document.children[0] as MarkdownNode.Blockquote
            assertTrue(quote.content.contains("bold"))
        }
    }
    
    @Nested
    @DisplayName("Complex Document Structure")
    inner class ComplexDocumentStructure {
        
        @Test
        @DisplayName("Complete markdown document should be parsed correctly")
        fun testCompleteDocument() {
            val markdown = """
                # Main Title
                
                This is an introduction paragraph with **bold** and *italic* text.
                
                ## Features
                
                - Feature 1
                - Feature 2
                - Feature 3
                
                ### Code Example
                
                Here's some `inline code` and a code block:
                
                ```kotlin
                fun main() {
                    println("Hello World")
                }
                ```
                
                ## Links and Images
                
                Check out [our website](https://example.com) and see this image:
                
                ![Example](https://example.com/image.png)
                
                > Remember: Always write good documentation!
                
                1. First step
                2. Second step
                3. Final step
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            
            // Verify document contains various node types
            val nodeTypes = document.children.map { it::class }.toSet()
            assertTrue(nodeTypes.any { it == MarkdownNode.Header::class })
            assertTrue(nodeTypes.any { it == MarkdownNode.Paragraph::class })
            assertTrue(nodeTypes.any { it == MarkdownNode.UnorderedList::class })
            assertTrue(nodeTypes.any { it == MarkdownNode.OrderedList::class })
            assertTrue(nodeTypes.any { it == MarkdownNode.CodeBlock::class })
            assertTrue(nodeTypes.any { it == MarkdownNode.Blockquote::class })
            
            // Verify headers
            val headers = document.children.filterIsInstance<MarkdownNode.Header>()
            assertTrue(headers.any { it.level == 1 && it.text == "Main Title" })
            assertTrue(headers.any { it.level == 2 && it.text == "Features" })
            assertTrue(headers.any { it.level == 3 && it.text == "Code Example" })
        }
        
        @Test
        @DisplayName("Nested structure should be handled correctly")
        fun testNestedStructure() {
            val markdown = """
                # Document
                
                Paragraph with **bold *nested italic* text**.
                
                - List with [link](url)
                - List with `code`
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            assertNotNull(document)
            assertTrue(document.children.isNotEmpty())
            
            // Verify that paragraphs can contain formatted text
            val paragraphs = document.children.filterIsInstance<MarkdownNode.Paragraph>()
            assertTrue(paragraphs.isNotEmpty())
        }
        
        @Test
        @DisplayName("Edge case: Empty lines and whitespace")
        fun testEmptyLinesAndWhitespace() {
            val markdown = """
                # Header
                
                
                Paragraph after empty lines.
                
                
                
                Another paragraph.
            """.trimIndent()
            
            val document = parseMarkdown(markdown)
            assertNotNull(document)
            
            val headers = document.children.filterIsInstance<MarkdownNode.Header>()
            val paragraphs = document.children.filterIsInstance<MarkdownNode.Paragraph>()
            
            assertEquals(1, headers.size)
            assertTrue(paragraphs.size >= 2)
        }
    }
    
    @Nested
    @DisplayName("Error Handling and Edge Cases")
    inner class ErrorHandlingAndEdgeCases {
        
        @Test
        @DisplayName("Malformed markdown should not crash parser")
        fun testMalformedMarkdown() {
            val malformedInputs = listOf(
                "**unclosed bold",
                "*unclosed italic",
                "~~unclosed strike",
                "`unclosed code",
                "[unclosed link",
                "![unclosed image",
                "```unclosed code block",
                "### ### ### multiple hashes"
            )
            
            for (input in malformedInputs) {
                assertDoesNotThrow("Failed for input: $input") {
                    val document = parseMarkdown(input)
                    assertNotNull(document)
                }
            }
        }
        
        @Test
        @DisplayName("Very long input should be handled")
        fun testVeryLongInput() {
            val longText = "word ".repeat(1000) + "**bold** " + "text ".repeat(1000)
            assertDoesNotThrow {
                val document = parseMarkdown(longText)
                assertNotNull(document)
            }
        }
        
        @Test
        @DisplayName("Special characters should be preserved")
        fun testSpecialCharacters() {
            val textWithSpecialChars = "Text with émojis 🎉 and spëcial çharacters"
            val document = parseMarkdown(textWithSpecialChars)
            
            assertNotNull(document)
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            val textNodes = paragraph.children.filterIsInstance<MarkdownNode.Text>()
            assertTrue(textNodes.any { it.content.contains("émojis") })
        }
        
        @Test
        @DisplayName("Mixed line endings should be handled")
        fun testMixedLineEndings() {
            val textWithMixedEndings = "Line 1\nLine 2\rLine 3\r\nLine 4"
            assertDoesNotThrow {
                val document = parseMarkdown(textWithMixedEndings)
                assertNotNull(document)
            }
        }
    }
    
    @Nested
    @DisplayName("AST Structure Validation")
    inner class ASTStructureValidation {
        
        @Test
        @DisplayName("Document should always be root node")
        fun testDocumentAsRoot() {
            val document = parseMarkdown("Any content")
            assertTrue(document is MarkdownNode.Document)
        }
        
        @Test
        @DisplayName("Headers should be direct children of document")
        fun testHeadersAsDirectChildren() {
            val document = parseMarkdown("# Header\nText")
            val headers = document.children.filterIsInstance<MarkdownNode.Header>()
            assertEquals(1, headers.size)
        }
        
        @Test
        @DisplayName("Inline elements should be children of paragraphs")
        fun testInlineElementsInParagraphs() {
            val document = parseMarkdown("**Bold** and *italic* text")
            assertEquals(1, document.children.size)
            assertTrue(document.children[0] is MarkdownNode.Paragraph)
            
            val paragraph = document.children[0] as MarkdownNode.Paragraph
            assertTrue(paragraph.children.any { it is MarkdownNode.Bold })
            assertTrue(paragraph.children.any { it is MarkdownNode.Italic })
        }
        
        @Test
        @DisplayName("Lists should contain list items")
        fun testListStructure() {
            val document = parseMarkdown("- Item 1\n- Item 2")
            val lists = document.children.filterIsInstance<MarkdownNode.UnorderedList>()
            assertEquals(1, lists.size)
            
            val list = lists[0]
            assertEquals(2, list.items.size)
            assertTrue(list.items.all { it is MarkdownNode.ListItem })
        }
    }
}