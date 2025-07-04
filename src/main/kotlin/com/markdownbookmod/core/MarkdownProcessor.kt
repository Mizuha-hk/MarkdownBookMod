package com.markdownbookmod.core

import com.markdownbookmod.core.interfaces.MarkdownVisitor
import com.markdownbookmod.core.models.Document

/**
 * Main facade for markdown processing
 */
class MarkdownProcessor {
    
    /**
     * Parse markdown text and convert to HTML
     */
    fun parseToHtml(markdown: String): String {
        return parse(markdown).accept(HtmlRenderer())
    }
    
    /**
     * Parse markdown text and convert to Minecraft formatted text
     */
    fun parseToMinecraftText(markdown: String): String {
        return parse(markdown).accept(MinecraftComponentRenderer())
    }
    
    /**
     * Parse markdown text to AST
     */
    fun parse(markdown: String): Document {
        return try {
            val lexer = MarkdownLexer(markdown)
            val tokens = lexer.tokenize()
            val parser = MarkdownParser(tokens)
            parser.parse()
        } catch (e: Exception) {
            // On error, treat as plain text
            Document(listOf(com.markdownbookmod.core.models.Text(markdown)))
        }
    }
    
    /**
     * Parse markdown text and render with custom visitor
     */
    fun parseAndRender(markdown: String, visitor: MarkdownVisitor): String {
        return parse(markdown).accept(visitor)
    }
}