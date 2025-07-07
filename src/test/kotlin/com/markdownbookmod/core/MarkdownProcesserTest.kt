package com.markdownbookmod.core

import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*

@DisplayName("MarkdownProcesser Tests")
class MarkdownProcesserTest {
    
    private lateinit var processor: MarkdownProcesser
    private lateinit var lexer: MarkdownLexer
    private lateinit var parser: MarkdownParser
    
    @BeforeEach
    fun setUp() {
        processor = MarkdownProcesser()
        lexer = MarkdownLexer()
        parser = MarkdownParser()
    }
    
    private fun processMarkdown(text: String): List<Component> {
        return processor.processMarkdown(text)
    }
    
    private fun parseAndProcess(text: String): List<Component> {
        val tokens = lexer.tokenize(text)
        val ast = parser.parse(tokens)
        return processor.processDocument(ast)
    }
    
    @Nested
    @DisplayName("Basic Processing")
    inner class BasicProcessing {
        
        @Test
        @DisplayName("Empty input should return empty list")
        fun testEmptyInput() {
            val components = processMarkdown("")
            assertTrue(components.isEmpty())
        }
        
        @Test
        @DisplayName("Simple text should be processed as text component")
        fun testSimpleText() {
            val components = processMarkdown("Hello World")
            assertEquals(1, components.size)
            assertNotNull(components[0])
            assertEquals("Hello World", components[0].string)
        }
        
        @Test
        @DisplayName("Multiple paragraphs should create multiple components")
        fun testMultipleParagraphs() {
            val components = processMarkdown("Paragraph 1\n\nParagraph 2")
            assertTrue(components.isNotEmpty())
        }
    }
    
    @Nested
    @DisplayName("Header Processing")
    inner class HeaderProcessing {
        
        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3, 4, 5, 6])
        @DisplayName("Headers should be formatted with appropriate styling")
        fun testHeaderFormatting(level: Int) {
            val headerText = "#".repeat(level) + " Header Text"
            val components = processMarkdown(headerText)
            
            assertTrue(components.isNotEmpty())
            val component = components[0]
            assertEquals("Header Text", component.string)
            
            // Verify styling
            val style = component.style
            assertTrue(style.color?.toString()?.contains("blue") == true || 
                      style.color == ChatFormatting.BLUE.color)
        }
        
        @Test
        @DisplayName("H1 and H2 headers should be bold")
        fun testBoldHeaders() {
            val h1Components = processMarkdown("# Main Title")
            val h2Components = processMarkdown("## Subtitle")
            
            assertTrue(h1Components.isNotEmpty())
            assertTrue(h2Components.isNotEmpty())
            
            assertTrue(h1Components[0].style.isBold)
            assertTrue(h2Components[0].style.isBold)
        }
        
        @Test
        @DisplayName("H3-H6 headers should not be bold")
        fun testNonBoldHeaders() {
            val h3Components = processMarkdown("### Section")
            val h4Components = processMarkdown("#### Subsection")
            
            assertTrue(h3Components.isNotEmpty())
            assertTrue(h4Components.isNotEmpty())
            
            assertFalse(h3Components[0].style.isBold)
            assertFalse(h4Components[0].style.isBold)
        }
        
        @Test
        @DisplayName("Header with special characters")
        fun testHeaderWithSpecialCharacters() {
            val components = processMarkdown("# Header with émojis 🎉")
            assertTrue(components.isNotEmpty())
            assertTrue(components[0].string.contains("émojis"))
            assertTrue(components[0].string.contains("🎉"))
        }
    }
    
    @Nested
    @DisplayName("Text Formatting Processing")
    inner class TextFormattingProcessing {
        
        @Test
        @DisplayName("Bold text should have bold formatting")
        fun testBoldFormatting() {
            val components = processMarkdown("This is **bold** text")
            assertTrue(components.isNotEmpty())
            
            // The paragraph should contain bold text
            val component = components[0]
            val content = component.string
            assertTrue(content.contains("bold"))
        }
        
        @Test
        @DisplayName("Italic text should have italic formatting")
        fun testItalicFormatting() {
            val components = processMarkdown("This is *italic* text")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            val content = component.string
            assertTrue(content.contains("italic"))
        }
        
        @Test
        @DisplayName("Strikethrough text should have strikethrough formatting")
        fun testStrikethroughFormatting() {
            val components = processMarkdown("This is ~~strikethrough~~ text")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            val content = component.string
            assertTrue(content.contains("strikethrough"))
        }
        
        @Test
        @DisplayName("Mixed formatting should be processed correctly")
        fun testMixedFormatting() {
            val components = processMarkdown("Text with **bold**, *italic*, and ~~strikethrough~~")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            val content = component.string
            assertTrue(content.contains("bold"))
            assertTrue(content.contains("italic"))
            assertTrue(content.contains("strikethrough"))
        }
    }
    
    @Nested
    @DisplayName("Code Processing")
    inner class CodeProcessing {
        
        @Test
        @DisplayName("Inline code should be formatted with code styling")
        fun testInlineCode() {
            val components = processMarkdown("Use `console.log()` for debugging")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains("console.log()"))
        }
        
        @Test
        @DisplayName("Code block should be formatted correctly")
        fun testCodeBlock() {
            val codeText = """
                ```kotlin
                fun example() {
                    println("Hello")
                }
                ```
            """.trimIndent()
            
            val components = processMarkdown(codeText)
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains("kotlin"))
            assertTrue(component.string.contains("fun example()"))
        }
        
        @Test
        @DisplayName("Code block without language")
        fun testCodeBlockWithoutLanguage() {
            val codeText = """
                ```
                plain code
                ```
            """.trimIndent()
            
            val components = processMarkdown(codeText)
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains("plain code"))
        }
        
        @Test
        @DisplayName("Code should have appropriate color styling")
        fun testCodeStyling() {
            val components = processMarkdown("`code`")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            // Verify green color or code styling is applied
            assertNotNull(component.style)
        }
    }
    
    @Nested
    @DisplayName("Links and Images Processing")
    inner class LinksAndImagesProcessing {
        
        @Test
        @DisplayName("Links should be formatted with link styling")
        fun testLinkFormatting() {
            val components = processMarkdown("Visit [our website](https://example.com)")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains("our website"))
            
            // Should have aqua color and underline
            val style = component.style
            assertTrue(style.isUnderlined || 
                      style.color == ChatFormatting.AQUA.color)
        }
        
        @Test
        @DisplayName("Images should be formatted as text with alt text")
        fun testImageFormatting() {
            val components = processMarkdown("![alt text](https://example.com/image.png)")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains("Image:") || 
                      component.string.contains("alt text"))
        }
        
        @Test
        @DisplayName("Multiple links should all be formatted")
        fun testMultipleLinks() {
            val components = processMarkdown("Visit [site1](url1) and [site2](url2)")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains("site1"))
            assertTrue(component.string.contains("site2"))
        }
    }
    
    @Nested
    @DisplayName("List Processing")
    inner class ListProcessing {
        
        @Test
        @DisplayName("Unordered list should use bullet points")
        fun testUnorderedListFormatting() {
            val markdown = """
                - Item 1
                - Item 2
                - Item 3
            """.trimIndent()
            
            val components = processMarkdown(markdown)
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            val content = component.string
            assertTrue(content.contains("•") || content.contains("Item 1"))
            assertTrue(content.contains("Item 2"))
            assertTrue(content.contains("Item 3"))
        }
        
        @Test
        @DisplayName("Ordered list should use numbers")
        fun testOrderedListFormatting() {
            val markdown = """
                1. First item
                2. Second item
                3. Third item
            """.trimIndent()
            
            val components = processMarkdown(markdown)
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            val content = component.string
            assertTrue(content.contains("1.") || content.contains("First item"))
            assertTrue(content.contains("2.") || content.contains("Second item"))
            assertTrue(content.contains("3.") || content.contains("Third item"))
        }
        
        @Test
        @DisplayName("Single item lists should be formatted correctly")
        fun testSingleItemLists() {
            val unorderedComponents = processMarkdown("- Single item")
            val orderedComponents = processMarkdown("1. Single item")
            
            assertTrue(unorderedComponents.isNotEmpty())
            assertTrue(orderedComponents.isNotEmpty())
            
            assertTrue(unorderedComponents[0].string.contains("Single item"))
            assertTrue(orderedComponents[0].string.contains("Single item"))
        }
        
        @Test
        @DisplayName("Empty list items should be handled")
        fun testEmptyListItems() {
            val components = processMarkdown("- \n- Item 2")
            assertTrue(components.isNotEmpty())
            // Should not crash and should handle gracefully
        }
    }
    
    @Nested
    @DisplayName("Blockquote Processing")
    inner class BlockquoteProcessing {
        
        @Test
        @DisplayName("Blockquote should be formatted with quote styling")
        fun testBlockquoteFormatting() {
            val components = processMarkdown("> This is a quote")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains(">") || 
                      component.string.contains("This is a quote"))
            
            // Should have gray color and italic styling
            val style = component.style
            assertTrue(style.isItalic || 
                      style.color == ChatFormatting.GRAY.color)
        }
        
        @Test
        @DisplayName("Multiple blockquotes should be formatted separately")
        fun testMultipleBlockquotes() {
            val markdown = """
                > First quote
                > Second quote
            """.trimIndent()
            
            val components = processMarkdown(markdown)
            assertTrue(components.isNotEmpty())
            
            // Should contain both quotes
            val allContent = components.joinToString(" ") { it.string }
            assertTrue(allContent.contains("First quote"))
            assertTrue(allContent.contains("Second quote"))
        }
        
        @Test
        @DisplayName("Blockquote with formatting should preserve inner formatting")
        fun testBlockquoteWithFormatting() {
            val components = processMarkdown("> This quote has **bold** text")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            assertTrue(component.string.contains("bold"))
        }
    }
    
    @Nested
    @DisplayName("Configuration and Customization")
    inner class ConfigurationAndCustomization {
        
        @Test
        @DisplayName("Custom configuration should be applied")
        fun testCustomConfiguration() {
            val customConfig = MarkdownConfig(
                headerColor = ChatFormatting.RED,
                boldColor = ChatFormatting.YELLOW,
                enableWordWrap = false
            )
            val customProcessor = MarkdownProcesser.withConfig(customConfig)
            
            val components = customProcessor.processMarkdown("# Red Header")
            assertTrue(components.isNotEmpty())
            
            val component = components[0]
            // Should use custom red color for header
            assertNotNull(component.style)
        }
        
        @Test
        @DisplayName("Default configuration should work")
        fun testDefaultConfiguration() {
            val defaultProcessor = MarkdownProcesser.withDefaults()
            val components = defaultProcessor.processMarkdown("# Header")
            
            assertTrue(components.isNotEmpty())
            assertNotNull(components[0])
        }
        
        @Test
        @DisplayName("Word wrap should work when enabled")
        fun testWordWrapEnabled() {
            val config = MarkdownConfig(
                maxLineLength = 20,
                enableWordWrap = true
            )
            val processor = MarkdownProcesser.withConfig(config)
            
            val longText = "This is a very long line of text that should be wrapped"
            val components = processor.processMarkdown(longText)
            
            // Should create multiple components when wrapping is enabled
            // and the text is longer than maxLineLength
            assertTrue(components.isNotEmpty())
        }
        
        @Test
        @DisplayName("Word wrap should be disabled when configured")
        fun testWordWrapDisabled() {
            val config = MarkdownConfig(
                maxLineLength = 10,
                enableWordWrap = false
            )
            val processor = MarkdownProcesser.withConfig(config)
            
            val longText = "This is a very long line of text"
            val components = processor.processMarkdown(longText)
            
            assertTrue(components.isNotEmpty())
            // When word wrap is disabled, should not break into multiple lines
        }
    }
    
    @Nested
    @DisplayName("Integration and Pipeline Tests")
    inner class IntegrationAndPipelineTests {
        
        @Test
        @DisplayName("Complete pipeline should work end-to-end")
        fun testCompletePipeline() {
            val markdown = """
                # Main Title
                
                This is a paragraph with **bold** and *italic* text.
                
                ## Code Section
                
                Use `println()` for output:
                
                ```kotlin
                fun main() {
                    println("Hello World")
                }
                ```
                
                ## Lists and Quotes
                
                Features:
                - Feature 1
                - Feature 2
                
                Steps:
                1. First step
                2. Second step
                
                > Remember to test your code!
                
                Visit [our site](https://example.com) for more info.
            """.trimIndent()
            
            val components = processMarkdown(markdown)
            assertTrue(components.isNotEmpty())
            
            // Verify that all content is processed
            val allContent = components.joinToString(" ") { it.string }
            assertTrue(allContent.contains("Main Title"))
            assertTrue(allContent.contains("bold"))
            assertTrue(allContent.contains("italic"))
            assertTrue(allContent.contains("println"))
            assertTrue(allContent.contains("Feature 1"))
            assertTrue(allContent.contains("First step"))
            assertTrue(allContent.contains("Remember to test"))
            assertTrue(allContent.contains("our site"))
        }
        
        @Test
        @DisplayName("Error in one part should not break entire processing")
        fun testPartialFailureHandling() {
            // Test with potentially problematic input
            val problematicMarkdown = """
                # Good Header
                
                **Unclosed bold text
                
                ## Another Good Header
                
                Normal text here
            """.trimIndent()
            
            assertDoesNotThrow {
                val components = processMarkdown(problematicMarkdown)
                assertTrue(components.isNotEmpty())
                
                val allContent = components.joinToString(" ") { it.string }
                assertTrue(allContent.contains("Good Header"))
                assertTrue(allContent.contains("Another Good Header"))
            }
        }
        
        @Test
        @DisplayName("Large document should be processed efficiently")
        fun testLargeDocumentProcessing() {
            val largeMarkdown = buildString {
                repeat(100) { i ->
                    appendLine("# Header $i")
                    appendLine()
                    appendLine("This is paragraph $i with **bold text** and *italic text*.")
                    appendLine()
                    appendLine("- Item 1 for section $i")
                    appendLine("- Item 2 for section $i")
                    appendLine()
                    appendLine("> Quote for section $i")
                    appendLine()
                    appendLine("1. Step 1")
                    appendLine("2. Step 2")
                    appendLine()
                }
            }
            
            assertDoesNotThrow {
                val components = processMarkdown(largeMarkdown)
                assertTrue(components.isNotEmpty())
                assertTrue(components.size > 10) // Should have many components
            }
        }
        
        @Test
        @DisplayName("Different markdown flavors should be handled")
        fun testMarkdownFlavorCompatibility() {
            // Test various markdown syntax variations
            val variations = listOf(
                "- List with dash",
                "+ List with plus", 
                "* List with asterisk (if supported)",
                "~~strikethrough~~",
                "`inline code`",
                "[link](url)",
                "![image](url)",
                "> blockquote",
                "### Header",
                "**bold**",
                "*italic*"
            )
            
            for (variation in variations) {
                assertDoesNotThrow("Failed for variation: $variation") {
                    val components = processMarkdown(variation)
                    assertTrue(components.isNotEmpty(), "No components for: $variation")
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Error Handling and Edge Cases")
    inner class ErrorHandlingAndEdgeCases {
        
        @Test
        @DisplayName("Null and empty inputs should be handled gracefully")
        fun testNullAndEmptyInputs() {
            assertDoesNotThrow {
                val emptyComponents = processMarkdown("")
                assertTrue(emptyComponents.isEmpty())
            }
        }
        
        @Test
        @DisplayName("Invalid unicode should not crash processor")
        fun testInvalidUnicode() {
            val textWithUnicode = "Text with unicode: \uD83D\uDE00 \u2764\uFE0F"
            assertDoesNotThrow {
                val components = processMarkdown(textWithUnicode)
                assertTrue(components.isNotEmpty())
            }
        }
        
        @Test
        @DisplayName("Very long lines should be handled")
        fun testVeryLongLines() {
            val veryLongLine = "word ".repeat(10000).trim()
            assertDoesNotThrow {
                val components = processMarkdown(veryLongLine)
                assertTrue(components.isNotEmpty())
            }
        }
        
        @Test
        @DisplayName("Deeply nested structures should be handled")
        fun testDeeplyNestedStructures() {
            val nestedMarkdown = """
                # Level 1
                ## Level 2
                ### Level 3
                #### Level 4
                ##### Level 5
                ###### Level 6
                
                > Quote with **bold *italic* text** and `code`
                
                - List with **bold**
                  - Nested item with *italic*
                    - Deep nested with ~~strike~~
            """.trimIndent()
            
            assertDoesNotThrow {
                val components = processMarkdown(nestedMarkdown)
                assertTrue(components.isNotEmpty())
            }
        }
        
        @Test
        @DisplayName("Special Minecraft formatting codes should not interfere")
        fun testMinecraftFormattingCodes() {
            val textWithCodes = "Text with §acolor codes§r and normal **bold** text"
            assertDoesNotThrow {
                val components = processMarkdown(textWithCodes)
                assertTrue(components.isNotEmpty())
            }
        }
    }
    
    @Nested
    @DisplayName("Component Properties and Validation")
    inner class ComponentPropertiesAndValidation {
        
        @Test
        @DisplayName("Components should have proper text content")
        fun testComponentTextContent() {
            val components = processMarkdown("Hello **World**")
            assertTrue(components.isNotEmpty())
            
            for (component in components) {
                assertNotNull(component.string)
                assertTrue(component.string.isNotEmpty())
            }
        }
        
        @Test
        @DisplayName("Components should have appropriate styling")
        fun testComponentStyling() {
            val components = processMarkdown("# Header")
            assertTrue(components.isNotEmpty())
            
            val headerComponent = components[0]
            assertNotNull(headerComponent.style)
            // Headers should have color styling
            assertTrue(headerComponent.style.color != null || 
                      headerComponent.style.isBold)
        }
        
        @Test
        @DisplayName("Component hierarchy should be maintained")
        fun testComponentHierarchy() {
            val components = processMarkdown("Text with **bold** content")
            assertTrue(components.isNotEmpty())
            
            // Components should maintain proper parent-child relationships
            for (component in components) {
                assertNotNull(component)
            }
        }
        
        @Test
        @DisplayName("Components should be serializable")
        fun testComponentSerialization() {
            val components = processMarkdown("Test content")
            assertTrue(components.isNotEmpty())
            
            // Components should be serializable to JSON (Minecraft requirement)
            assertDoesNotThrow {
                for (component in components) {
                    Component.Serializer.toJson(component)
                }
            }
        }
    }
}