package com.markdownbookmod.core

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.ChatFormatting

/**
 * Configuration for markdown processing
 */
data class MarkdownConfig(
    val headerColor: ChatFormatting = ChatFormatting.BLUE,
    val boldColor: ChatFormatting = ChatFormatting.WHITE,
    val italicColor: ChatFormatting = ChatFormatting.WHITE,
    val strikethroughColor: ChatFormatting = ChatFormatting.GRAY,
    val codeColor: ChatFormatting = ChatFormatting.GREEN,
    val linkColor: ChatFormatting = ChatFormatting.AQUA,
    val quoteColor: ChatFormatting = ChatFormatting.GRAY,
    val listColor: ChatFormatting = ChatFormatting.WHITE,
    val maxLineLength: Int = 50,
    val enableWordWrap: Boolean = true
)

/**
 * Processor for converting parsed markdown AST into Minecraft text components
 */
class MarkdownProcesser(private val config: MarkdownConfig = MarkdownConfig()) {
    
    /**
     * Process markdown text through the complete pipeline
     * @param markdownText Raw markdown text
     * @return List of Minecraft text components
     */
    fun processMarkdown(markdownText: String): List<Component> {
        val lexer = MarkdownLexer()
        val parser = MarkdownParser()
        
        val tokens = lexer.tokenize(markdownText)
        val ast = parser.parse(tokens)
        
        return processDocument(ast)
    }
    
    /**
     * Process a parsed document node into Minecraft components
     * @param document The parsed document AST
     * @return List of Minecraft text components
     */
    fun processDocument(document: MarkdownNode.Document): List<Component> {
        val components = mutableListOf<Component>()
        
        for (node in document.children) {
            val component = processNode(node)
            if (component != null) {
                if (config.enableWordWrap && component is MutableComponent) {
                    components.addAll(wrapText(component))
                } else {
                    components.add(component)
                }
            }
        }
        
        return components
    }
    
    /**
     * Process a single AST node into a Minecraft component
     */
    private fun processNode(node: MarkdownNode): Component? {
        return when (node) {
            is MarkdownNode.Document -> {
                // This shouldn't happen at this level
                null
            }
            is MarkdownNode.Header -> {
                Component.literal(node.text)
                    .withStyle(config.headerColor)
                    .withStyle { style ->
                        when (node.level) {
                            1 -> style.withBold(true)
                            2 -> style.withBold(true)
                            else -> style
                        }
                    }
            }
            is MarkdownNode.Paragraph -> {
                val components = mutableListOf<Component>()
                var currentComponent = Component.empty()
                
                for (child in node.children) {
                    val childComponent = processNode(child)
                    if (childComponent != null) {
                        currentComponent = currentComponent.copy().append(childComponent)
                    }
                }
                
                currentComponent
            }
            is MarkdownNode.Bold -> {
                Component.literal(node.text)
                    .withStyle(config.boldColor)
                    .withStyle(ChatFormatting.BOLD)
            }
            is MarkdownNode.Italic -> {
                Component.literal(node.text)
                    .withStyle(config.italicColor)
                    .withStyle(ChatFormatting.ITALIC)
            }
            is MarkdownNode.Strikethrough -> {
                Component.literal(node.text)
                    .withStyle(config.strikethroughColor)
                    .withStyle(ChatFormatting.STRIKETHROUGH)
            }
            is MarkdownNode.CodeInline -> {
                Component.literal(node.code)
                    .withStyle(config.codeColor)
            }
            is MarkdownNode.CodeBlock -> {
                val language = if (node.language.isNotEmpty()) "[${node.language}]" else ""
                Component.literal("$language\n${node.code}")
                    .withStyle(config.codeColor)
            }
            is MarkdownNode.Link -> {
                Component.literal(node.text)
                    .withStyle(config.linkColor)
                    .withStyle(ChatFormatting.UNDERLINE)
            }
            is MarkdownNode.Image -> {
                Component.literal("[Image: ${node.altText}]")
                    .withStyle(config.linkColor)
            }
            is MarkdownNode.UnorderedList -> {
                val listComponent = Component.empty()
                for ((index, item) in node.items.withIndex()) {
                    val itemComponent = Component.literal("• ${item.content}")
                        .withStyle(config.listColor)
                    listComponent.append(itemComponent)
                    if (index < node.items.size - 1) {
                        listComponent.append(Component.literal("\n"))
                    }
                }
                listComponent
            }
            is MarkdownNode.OrderedList -> {
                val listComponent = Component.empty()
                for ((index, item) in node.items.withIndex()) {
                    val itemComponent = Component.literal("${index + 1}. ${item.content}")
                        .withStyle(config.listColor)
                    listComponent.append(itemComponent)
                    if (index < node.items.size - 1) {
                        listComponent.append(Component.literal("\n"))
                    }
                }
                listComponent
            }
            is MarkdownNode.ListItem -> {
                // This should be handled by the list processors
                Component.literal(node.content)
                    .withStyle(config.listColor)
            }
            is MarkdownNode.Blockquote -> {
                Component.literal("> ${node.content}")
                    .withStyle(config.quoteColor)
                    .withStyle(ChatFormatting.ITALIC)
            }
            is MarkdownNode.Text -> {
                Component.literal(node.content)
            }
            is MarkdownNode.LineBreak -> {
                Component.literal("\n")
            }
        }
    }
    
    /**
     * Wrap text to fit within configured line length
     */
    private fun wrapText(component: Component): List<Component> {
        if (!config.enableWordWrap) {
            return listOf(component)
        }
        
        val text = component.string
        if (text.length <= config.maxLineLength) {
            return listOf(component)
        }
        
        val lines = mutableListOf<Component>()
        val words = text.split(' ')
        var currentLine = StringBuilder()
        
        for (word in words) {
            if (currentLine.length + word.length + 1 > config.maxLineLength) {
                if (currentLine.isNotEmpty()) {
                    lines.add(Component.literal(currentLine.toString().trim())
                        .withStyle(component.style))
                    currentLine.clear()
                }
            }
            
            if (currentLine.isNotEmpty()) {
                currentLine.append(' ')
            }
            currentLine.append(word)
        }
        
        if (currentLine.isNotEmpty()) {
            lines.add(Component.literal(currentLine.toString())
                .withStyle(component.style))
        }
        
        return lines
    }
    
    /**
     * Create a processor with custom configuration
     */
    companion object {
        fun withConfig(config: MarkdownConfig): MarkdownProcesser {
            return MarkdownProcesser(config)
        }
        
        fun withDefaults(): MarkdownProcesser {
            return MarkdownProcesser()
        }
    }
}