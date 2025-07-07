package com.markdownbookmod.parser

import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertIs

@DisplayName("MarkdownProcesser Tests")
class MarkdownProcesserTest {
    
    private lateinit var processor: MarkdownProcesser
    private lateinit var config: MarkdownProcesser.ProcessorConfig
    
    @BeforeEach
    fun setUp() {
        config = MarkdownProcesser.ProcessorConfig()
        processor = MarkdownProcesser(config)
    }
    
    @Nested
    @DisplayName("Configuration Tests")
    inner class ConfigurationTests {
        
        @Test
        @DisplayName("Should use default configuration")
        fun shouldUseDefaultConfiguration() {
            val defaultProcessor = MarkdownProcesser()
            assertNotNull(defaultProcessor)
        }
        
        @Test
        @DisplayName("Should use custom configuration")
        fun shouldUseCustomConfiguration() {
            val customConfig = MarkdownProcesser.ProcessorConfig(
                maxLineLength = 100,
                boldColor = ChatFormatting.RED
            )
            val customProcessor = MarkdownProcesser(customConfig)
            assertNotNull(customProcessor)
        }
    }
    
    @Nested
    @DisplayName("Text Node Processing")
    inner class TextNodeProcessing {
        
        @Test
        @DisplayName("Should process simple text node")
        fun shouldProcessSimpleTextNode() {
            val textNode = MarkdownParser.ASTNode.Text("Simple text")
            val components = processor.process(textNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process text with special characters")
        fun shouldProcessTextWithSpecialCharacters() {
            val textNode = MarkdownParser.ASTNode.Text("Text with & < > symbols")
            val components = processor.process(textNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process empty text")
        fun shouldProcessEmptyText() {
            val textNode = MarkdownParser.ASTNode.Text("")
            val components = processor.process(textNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Header Processing")
    inner class HeaderProcessing {
        
        @Test
        @DisplayName("Should process H1 header")
        fun shouldProcessH1Header() {
            val headerNode = MarkdownParser.ASTNode.Header(1, "Header 1")
            val components = processor.process(headerNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process H6 header")
        fun shouldProcessH6Header() {
            val headerNode = MarkdownParser.ASTNode.Header(6, "Header 6")
            val components = processor.process(headerNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process header with special characters")
        fun shouldProcessHeaderWithSpecialCharacters() {
            val headerNode = MarkdownParser.ASTNode.Header(2, "Header with *symbols*")
            val components = processor.process(headerNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Text Formatting Processing")
    inner class TextFormattingProcessing {
        
        @Test
        @DisplayName("Should process bold text")
        fun shouldProcessBoldText() {
            val boldNode = MarkdownParser.ASTNode.Bold("Bold text")
            val components = processor.process(boldNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process italic text")
        fun shouldProcessItalicText() {
            val italicNode = MarkdownParser.ASTNode.Italic("Italic text")
            val components = processor.process(italicNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process strikethrough text")
        fun shouldProcessStrikethroughText() {
            val strikethroughNode = MarkdownParser.ASTNode.Strikethrough("Strikethrough text")
            val components = processor.process(strikethroughNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process inline code")
        fun shouldProcessInlineCode() {
            val codeNode = MarkdownParser.ASTNode.InlineCode("inline code")
            val components = processor.process(codeNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Code Block Processing")
    inner class CodeBlockProcessing {
        
        @Test
        @DisplayName("Should process simple code block")
        fun shouldProcessSimpleCodeBlock() {
            val codeBlockNode = MarkdownParser.ASTNode.CodeBlock("println(\"Hello\")")
            val components = processor.process(codeBlockNode)
            
            assertTrue(components.size >= 3) // opening ```, content, closing ```
        }
        
        @Test
        @DisplayName("Should process code block with language")
        fun shouldProcessCodeBlockWithLanguage() {
            val codeBlockNode = MarkdownParser.ASTNode.CodeBlock("println(\"Hello\")", "kotlin")
            val components = processor.process(codeBlockNode)
            
            assertTrue(components.size >= 3)
        }
        
        @Test
        @DisplayName("Should process multiline code block")
        fun shouldProcessMultilineCodeBlock() {
            val codeContent = """fun test() {
                |    println("Hello")
                |    return true
                |}""".trimMargin()
            
            val codeBlockNode = MarkdownParser.ASTNode.CodeBlock(codeContent, "kotlin")
            val components = processor.process(codeBlockNode)
            
            assertTrue(components.size > 3) // Should have multiple lines
        }
        
        @Test
        @DisplayName("Should handle long lines in code block")
        fun shouldHandleLongLinesInCodeBlock() {
            val longLine = "a".repeat(200) // Very long line
            val codeBlockNode = MarkdownParser.ASTNode.CodeBlock(longLine)
            val components = processor.process(codeBlockNode)
            
            assertTrue(components.size >= 3)
        }
    }
    
    @Nested
    @DisplayName("Link and Image Processing")
    inner class LinkAndImageProcessing {
        
        @Test
        @DisplayName("Should process link")
        fun shouldProcessLink() {
            val linkNode = MarkdownParser.ASTNode.Link("GitHub", "https://github.com")
            val components = processor.process(linkNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process link with empty text")
        fun shouldProcessLinkWithEmptyText() {
            val linkNode = MarkdownParser.ASTNode.Link("", "https://example.com")
            val components = processor.process(linkNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process image")
        fun shouldProcessImage() {
            val imageNode = MarkdownParser.ASTNode.Image("Alt text", "image.png")
            val components = processor.process(imageNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process image with empty alt text")
        fun shouldProcessImageWithEmptyAltText() {
            val imageNode = MarkdownParser.ASTNode.Image("", "image.png")
            val components = processor.process(imageNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("List Processing")
    inner class ListProcessing {
        
        @Test
        @DisplayName("Should process single list item")
        fun shouldProcessSingleListItem() {
            val listItemNode = MarkdownParser.ASTNode.ListItem("List item")
            val components = processor.process(listItemNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process unordered list")
        fun shouldProcessUnorderedList() {
            val items = listOf(
                MarkdownParser.ASTNode.ListItem("Item 1"),
                MarkdownParser.ASTNode.ListItem("Item 2"),
                MarkdownParser.ASTNode.ListItem("Item 3")
            )
            val listNode = MarkdownParser.ASTNode.UnorderedList(items)
            val components = processor.process(listNode)
            
            assertEquals(3, components.size)
            components.forEach { assertNotNull(it) }
        }
        
        @Test
        @DisplayName("Should process empty list")
        fun shouldProcessEmptyList() {
            val listNode = MarkdownParser.ASTNode.UnorderedList(emptyList())
            val components = processor.process(listNode)
            
            assertEquals(0, components.size)
        }
        
        @Test
        @DisplayName("Should process list with long items")
        fun shouldProcessListWithLongItems() {
            val longText = "This is a very long list item that might need to be wrapped to fit within the configured line length limit."
            val items = listOf(MarkdownParser.ASTNode.ListItem(longText))
            val listNode = MarkdownParser.ASTNode.UnorderedList(items)
            val components = processor.process(listNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Blockquote Processing")
    inner class BlockquoteProcessing {
        
        @Test
        @DisplayName("Should process simple blockquote")
        fun shouldProcessSimpleBlockquote() {
            val blockquoteNode = MarkdownParser.ASTNode.Blockquote("This is a quote")
            val components = processor.process(blockquoteNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process empty blockquote")
        fun shouldProcessEmptyBlockquote() {
            val blockquoteNode = MarkdownParser.ASTNode.Blockquote("")
            val components = processor.process(blockquoteNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process long blockquote")
        fun shouldProcessLongBlockquote() {
            val longQuote = "This is a very long blockquote that should be processed correctly even if it exceeds the normal line length limits."
            val blockquoteNode = MarkdownParser.ASTNode.Blockquote(longQuote)
            val components = processor.process(blockquoteNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Paragraph Processing")
    inner class ParagraphProcessing {
        
        @Test
        @DisplayName("Should process simple paragraph")
        fun shouldProcessSimpleParagraph() {
            val textNode = MarkdownParser.ASTNode.Text("Simple text")
            val paragraphNode = MarkdownParser.ASTNode.Paragraph(listOf(textNode))
            val components = processor.process(paragraphNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process paragraph with mixed content")
        fun shouldProcessParagraphWithMixedContent() {
            val children = listOf(
                MarkdownParser.ASTNode.Text("This is "),
                MarkdownParser.ASTNode.Bold("bold"),
                MarkdownParser.ASTNode.Text(" and "),
                MarkdownParser.ASTNode.Italic("italic"),
                MarkdownParser.ASTNode.Text(" text.")
            )
            val paragraphNode = MarkdownParser.ASTNode.Paragraph(children)
            val components = processor.process(paragraphNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should process empty paragraph")
        fun shouldProcessEmptyParagraph() {
            val paragraphNode = MarkdownParser.ASTNode.Paragraph(emptyList())
            val components = processor.process(paragraphNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Document Processing")
    inner class DocumentProcessing {
        
        @Test
        @DisplayName("Should process simple document")
        fun shouldProcessSimpleDocument() {
            val children = listOf(
                MarkdownParser.ASTNode.Header(1, "Header"),
                MarkdownParser.ASTNode.Paragraph(listOf(MarkdownParser.ASTNode.Text("Content")))
            )
            val documentNode = MarkdownParser.ASTNode.Document(children)
            val components = processor.process(documentNode)
            
            assertEquals(2, components.size)
            components.forEach { assertNotNull(it) }
        }
        
        @Test
        @DisplayName("Should process empty document")
        fun shouldProcessEmptyDocument() {
            val documentNode = MarkdownParser.ASTNode.Document(emptyList())
            val components = processor.process(documentNode)
            
            assertEquals(0, components.size)
        }
        
        @Test
        @DisplayName("Should process complex document")
        fun shouldProcessComplexDocument() {
            val children = listOf(
                MarkdownParser.ASTNode.Header(1, "Title"),
                MarkdownParser.ASTNode.Paragraph(listOf(
                    MarkdownParser.ASTNode.Text("This is "),
                    MarkdownParser.ASTNode.Bold("bold"),
                    MarkdownParser.ASTNode.Text(" text.")
                )),
                MarkdownParser.ASTNode.UnorderedList(listOf(
                    MarkdownParser.ASTNode.ListItem("Item 1"),
                    MarkdownParser.ASTNode.ListItem("Item 2")
                )),
                MarkdownParser.ASTNode.CodeBlock("code example", "kotlin"),
                MarkdownParser.ASTNode.Blockquote("Important note")
            )
            val documentNode = MarkdownParser.ASTNode.Document(children)
            val components = processor.process(documentNode)
            
            assertTrue(components.size >= 5)
            components.forEach { assertNotNull(it) }
        }
    }
    
    @Nested
    @DisplayName("Line Break Processing")
    inner class LineBreakProcessing {
        
        @Test
        @DisplayName("Should process line break")
        fun shouldProcessLineBreak() {
            val components = processor.process(MarkdownParser.ASTNode.LineBreak)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Full Pipeline Processing")
    inner class FullPipelineProcessing {
        
        @Test
        @DisplayName("Should process simple markdown")
        fun shouldProcessSimpleMarkdown() {
            val markdown = "# Header\nThis is **bold** text."
            val components = processor.processMarkdown(markdown)
            
            assertTrue(components.isNotEmpty())
            components.forEach { assertNotNull(it) }
        }
        
        @Test
        @DisplayName("Should process complex markdown")
        fun shouldProcessComplexMarkdown() {
            val markdown = """# Title
                |This is **bold** and *italic* text.
                |
                |> This is a blockquote
                |
                |- List item 1
                |- List item 2
                |
                |[Link](https://example.com)
                |
                |```kotlin
                |fun main() {
                |    println("Hello")
                |}
                |```""".trimMargin()
            
            val components = processor.processMarkdown(markdown)
            
            assertTrue(components.isNotEmpty())
            components.forEach { assertNotNull(it) }
        }
        
        @Test
        @DisplayName("Should handle empty markdown")
        fun shouldHandleEmptyMarkdown() {
            val components = processor.processMarkdown("")
            
            assertEquals(0, components.size)
        }
        
        @Test
        @DisplayName("Should get formatted text sequences")
        fun shouldGetFormattedTextSequences() {
            val markdown = "# Header\nSimple text."
            val sequences = processor.getFormattedText(markdown)
            
            assertTrue(sequences.isNotEmpty())
            sequences.forEach { assertNotNull(it) }
        }
        
        @Test
        @DisplayName("Should get single component")
        fun shouldGetSingleComponent() {
            val markdown = "# Header\nSimple text."
            val component = processor.getSingleComponent(markdown)
            
            assertNotNull(component)
        }
        
        @Test
        @DisplayName("Should handle markdown with special characters")
        fun shouldHandleMarkdownWithSpecialCharacters() {
            val markdown = "Text with & < > \" ' symbols"
            val components = processor.processMarkdown(markdown)
            
            assertTrue(components.isNotEmpty())
            assertNotNull(components[0])
        }
    }
    
    @Nested
    @DisplayName("Line Wrapping Tests")
    inner class LineWrappingTests {
        
        @Test
        @DisplayName("Should wrap long lines in code blocks")
        fun shouldWrapLongLinesInCodeBlocks() {
            val shortLineConfig = MarkdownProcesser.ProcessorConfig(maxLineLength = 20)
            val shortLineProcessor = MarkdownProcesser(shortLineConfig)
            
            val longCode = "This is a very long line of code that should be wrapped"
            val codeBlockNode = MarkdownParser.ASTNode.CodeBlock(longCode)
            val components = shortLineProcessor.process(codeBlockNode)
            
            assertTrue(components.size > 3) // Should have multiple wrapped lines
        }
        
        @Test
        @DisplayName("Should handle normal length lines")
        fun shouldHandleNormalLengthLines() {
            val codeBlockNode = MarkdownParser.ASTNode.CodeBlock("short")
            val components = processor.process(codeBlockNode)
            
            assertEquals(3, components.size) // opening ```, content, closing ```
        }
    }
    
    @Nested
    @DisplayName("Custom Configuration Tests")
    inner class CustomConfigurationTests {
        
        @Test
        @DisplayName("Should use custom colors")
        fun shouldUseCustomColors() {
            val customConfig = MarkdownProcesser.ProcessorConfig(
                boldColor = ChatFormatting.RED,
                italicColor = ChatFormatting.GREEN,
                linkColor = ChatFormatting.YELLOW
            )
            val customProcessor = MarkdownProcesser(customConfig)
            
            val boldNode = MarkdownParser.ASTNode.Bold("Bold text")
            val components = customProcessor.process(boldNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should use custom header colors")
        fun shouldUseCustomHeaderColors() {
            val customConfig = MarkdownProcesser.ProcessorConfig(
                headerColors = mapOf(
                    1 to ChatFormatting.RED,
                    2 to ChatFormatting.GREEN,
                    3 to ChatFormatting.BLUE
                )
            )
            val customProcessor = MarkdownProcesser(customConfig)
            
            val headerNode = MarkdownParser.ASTNode.Header(1, "Red Header")
            val components = customProcessor.process(headerNode)
            
            assertEquals(1, components.size)
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Should use custom line length")
        fun shouldUseCustomLineLength() {
            val customConfig = MarkdownProcesser.ProcessorConfig(maxLineLength = 10)
            val customProcessor = MarkdownProcesser(customConfig)
            
            val longLine = "This is a very long line that should be wrapped"
            val codeBlockNode = MarkdownParser.ASTNode.CodeBlock(longLine)
            val components = customProcessor.process(codeBlockNode)
            
            assertTrue(components.size > 3) // Should have multiple wrapped lines
        }
    }
}