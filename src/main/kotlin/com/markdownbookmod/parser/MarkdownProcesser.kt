package com.markdownbookmod.parser

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence
import net.minecraft.ChatFormatting

/**
 * Processor for converting Markdown AST to Minecraft text components.
 * Handles the conversion from parsed markdown to displayable text in Minecraft.
 */
class MarkdownProcesser {
    
    /**
     * Configuration for markdown processing
     */
    data class ProcessorConfig(
        val maxLineLength: Int = 80,
        val headerColors: Map<Int, ChatFormatting> = mapOf(
            1 to ChatFormatting.DARK_BLUE,
            2 to ChatFormatting.BLUE,
            3 to ChatFormatting.AQUA,
            4 to ChatFormatting.GREEN,
            5 to ChatFormatting.YELLOW,
            6 to ChatFormatting.GOLD
        ),
        val boldColor: ChatFormatting = ChatFormatting.WHITE,
        val italicColor: ChatFormatting = ChatFormatting.WHITE,
        val codeColor: ChatFormatting = ChatFormatting.GRAY,
        val linkColor: ChatFormatting = ChatFormatting.BLUE,
        val strikethroughColor: ChatFormatting = ChatFormatting.GRAY,
        val blockquoteColor: ChatFormatting = ChatFormatting.GRAY,
        val listColor: ChatFormatting = ChatFormatting.WHITE
    )
    
    private val config: ProcessorConfig
    
    constructor(config: ProcessorConfig = ProcessorConfig()) {
        this.config = config
    }
    
    /**
     * Process a markdown AST node into Minecraft text components
     */
    fun process(node: MarkdownParser.ASTNode): List<Component> {
        return when (node) {
            is MarkdownParser.ASTNode.Document -> processDocument(node)
            is MarkdownParser.ASTNode.Header -> listOf(processHeader(node))
            is MarkdownParser.ASTNode.Paragraph -> listOf(processParagraph(node))
            is MarkdownParser.ASTNode.Bold -> listOf(processBold(node))
            is MarkdownParser.ASTNode.Italic -> listOf(processItalic(node))
            is MarkdownParser.ASTNode.Strikethrough -> listOf(processStrikethrough(node))
            is MarkdownParser.ASTNode.InlineCode -> listOf(processInlineCode(node))
            is MarkdownParser.ASTNode.CodeBlock -> processCodeBlock(node)
            is MarkdownParser.ASTNode.Link -> listOf(processLink(node))
            is MarkdownParser.ASTNode.Image -> listOf(processImage(node))
            is MarkdownParser.ASTNode.ListItem -> listOf(processListItem(node))
            is MarkdownParser.ASTNode.UnorderedList -> processUnorderedList(node)
            is MarkdownParser.ASTNode.Blockquote -> listOf(processBlockquote(node))
            is MarkdownParser.ASTNode.Text -> listOf(processText(node))
            is MarkdownParser.ASTNode.LineBreak -> listOf(Component.literal(""))
        }
    }
    
    /**
     * Process a document node
     */
    private fun processDocument(document: MarkdownParser.ASTNode.Document): List<Component> {
        val components = mutableListOf<Component>()
        
        for (child in document.children) {
            components.addAll(process(child))
        }
        
        return components
    }
    
    /**
     * Process a header node
     */
    private fun processHeader(header: MarkdownParser.ASTNode.Header): Component {
        val color = config.headerColors[header.level] ?: ChatFormatting.WHITE
        val style = Style.EMPTY.withColor(color).withBold(true)
        
        return Component.literal(header.text).withStyle(style)
    }
    
    /**
     * Process a paragraph node
     */
    private fun processParagraph(paragraph: MarkdownParser.ASTNode.Paragraph): Component {
        val component = Component.empty()
        
        for (child in paragraph.children) {
            val childComponents = process(child)
            for (childComponent in childComponents) {
                component.append(childComponent)
            }
        }
        
        return component
    }
    
    /**
     * Process a bold node
     */
    private fun processBold(bold: MarkdownParser.ASTNode.Bold): Component {
        val style = Style.EMPTY.withColor(config.boldColor).withBold(true)
        return Component.literal(bold.text).withStyle(style)
    }
    
    /**
     * Process an italic node
     */
    private fun processItalic(italic: MarkdownParser.ASTNode.Italic): Component {
        val style = Style.EMPTY.withColor(config.italicColor).withItalic(true)
        return Component.literal(italic.text).withStyle(style)
    }
    
    /**
     * Process a strikethrough node
     */
    private fun processStrikethrough(strikethrough: MarkdownParser.ASTNode.Strikethrough): Component {
        val style = Style.EMPTY.withColor(config.strikethroughColor).withStrikethrough(true)
        return Component.literal(strikethrough.text).withStyle(style)
    }
    
    /**
     * Process an inline code node
     */
    private fun processInlineCode(code: MarkdownParser.ASTNode.InlineCode): Component {
        val style = Style.EMPTY.withColor(config.codeColor)
        return Component.literal("`${code.text}`").withStyle(style)
    }
    
    /**
     * Process a code block node
     */
    private fun processCodeBlock(codeBlock: MarkdownParser.ASTNode.CodeBlock): List<Component> {
        val components = mutableListOf<Component>()
        val style = Style.EMPTY.withColor(config.codeColor)
        
        // Add language indicator if present
        if (codeBlock.language.isNotEmpty()) {
            components.add(Component.literal("```${codeBlock.language}").withStyle(style))
        } else {
            components.add(Component.literal("```").withStyle(style))
        }
        
        // Split code into lines and wrap if necessary
        val lines = codeBlock.text.split('\n')
        for (line in lines) {
            val wrappedLines = wrapLine(line, config.maxLineLength)
            for (wrappedLine in wrappedLines) {
                components.add(Component.literal(wrappedLine).withStyle(style))
            }
        }
        
        components.add(Component.literal("```").withStyle(style))
        return components
    }
    
    /**
     * Process a link node
     */
    private fun processLink(link: MarkdownParser.ASTNode.Link): Component {
        val style = Style.EMPTY.withColor(config.linkColor).withUnderlined(true)
        return Component.literal(link.text).withStyle(style)
    }
    
    /**
     * Process an image node
     */
    private fun processImage(image: MarkdownParser.ASTNode.Image): Component {
        val style = Style.EMPTY.withColor(ChatFormatting.YELLOW)
        return Component.literal("[Image: ${image.altText}]").withStyle(style)
    }
    
    /**
     * Process a list item node
     */
    private fun processListItem(listItem: MarkdownParser.ASTNode.ListItem): Component {
        val style = Style.EMPTY.withColor(config.listColor)
        return Component.literal("• ${listItem.text}").withStyle(style)
    }
    
    /**
     * Process an unordered list node
     */
    private fun processUnorderedList(list: MarkdownParser.ASTNode.UnorderedList): List<Component> {
        val components = mutableListOf<Component>()
        
        for (item in list.items) {
            components.add(processListItem(item))
        }
        
        return components
    }
    
    /**
     * Process a blockquote node
     */
    private fun processBlockquote(blockquote: MarkdownParser.ASTNode.Blockquote): Component {
        val style = Style.EMPTY.withColor(config.blockquoteColor).withItalic(true)
        return Component.literal("> ${blockquote.text}").withStyle(style)
    }
    
    /**
     * Process a text node
     */
    private fun processText(text: MarkdownParser.ASTNode.Text): Component {
        return Component.literal(text.content)
    }
    
    /**
     * Wrap a line to fit within the specified length
     */
    private fun wrapLine(line: String, maxLength: Int): List<String> {
        if (line.length <= maxLength) {
            return listOf(line)
        }
        
        val words = line.split(' ')
        val wrappedLines = mutableListOf<String>()
        var currentLine = StringBuilder()
        
        for (word in words) {
            if (currentLine.length + word.length + 1 <= maxLength) {
                if (currentLine.isNotEmpty()) {
                    currentLine.append(' ')
                }
                currentLine.append(word)
            } else {
                if (currentLine.isNotEmpty()) {
                    wrappedLines.add(currentLine.toString())
                    currentLine = StringBuilder(word)
                } else {
                    // Word is longer than maxLength, split it
                    wrappedLines.add(word.substring(0, maxLength))
                    currentLine = StringBuilder(word.substring(maxLength))
                }
            }
        }
        
        if (currentLine.isNotEmpty()) {
            wrappedLines.add(currentLine.toString())
        }
        
        return wrappedLines
    }
    
    /**
     * Full processing pipeline: text -> tokens -> AST -> components
     */
    fun processMarkdown(markdownText: String): List<Component> {
        val lexer = MarkdownLexer()
        val tokens = lexer.tokenize(markdownText)
        
        val parser = MarkdownParser()
        val ast = parser.parse(tokens)
        
        return process(ast)
    }
    
    /**
     * Get the formatted character sequences for rendering
     */
    fun getFormattedText(markdownText: String): List<FormattedCharSequence> {
        val components = processMarkdown(markdownText)
        return components.map { it.visualOrderText }
    }
    
    /**
     * Get a single component containing all processed text
     */
    fun getSingleComponent(markdownText: String): Component {
        val components = processMarkdown(markdownText)
        val mainComponent = Component.empty()
        
        for (i in components.indices) {
            mainComponent.append(components[i])
            if (i < components.size - 1) {
                mainComponent.append(Component.literal("\n"))
            }
        }
        
        return mainComponent
    }
}