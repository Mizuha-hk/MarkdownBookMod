package com.markdownbookmod.core

import com.markdownbookmod.core.models.*

/**
 * Parser for converting tokens to AST
 */
class MarkdownParser(private val tokens: List<Token>) {
    private var position = 0
    
    fun parse(): Document {
        val elements = mutableListOf<MarkdownNode>()
        
        while (!isAtEnd()) {
            // Skip whitespace and newlines at document level
            if (current().type == TokenType.WHITESPACE || 
                current().type == TokenType.NEWLINE) {
                advance()
                continue
            }
            
            val element = parseElement()
            if (element != null) {
                elements.add(element)
            }
        }
        
        return Document(elements)
    }
    
    private fun parseElement(): MarkdownNode? {
        return when (current().type) {
            TokenType.HEADING_1, TokenType.HEADING_2, TokenType.HEADING_3,
            TokenType.HEADING_4, TokenType.HEADING_5 -> parseHeading()
            TokenType.BULLET_POINT -> parseBulletList()
            TokenType.NUMBERED_POINT -> parseOrderedList()
            TokenType.TEXT, TokenType.BOLD, TokenType.ITALIC, TokenType.STRIKETHROUGH -> parseParagraph()
            else -> {
                advance() // Skip unknown tokens
                null
            }
        }
    }
    
    private fun parseHeading(): Heading {
        val token = current()
        advance()
        
        val level = when (token.type) {
            TokenType.HEADING_1 -> 1
            TokenType.HEADING_2 -> 2
            TokenType.HEADING_3 -> 3
            TokenType.HEADING_4 -> 4
            TokenType.HEADING_5 -> 5
            else -> 1
        }
        
        // Skip whitespace after #
        skipWhitespace()
        
        // Collect text until end of line
        val textParts = mutableListOf<String>()
        while (!isAtEnd() && current().type != TokenType.NEWLINE && current().type != TokenType.EOF) {
            if (current().type == TokenType.TEXT) {
                textParts.add(current().value)
            }
            advance()
        }
        
        return Heading(level, textParts.joinToString(""))
    }
    
    private fun parseBulletList(): BulletList {
        val items = mutableListOf<ListItem>()
        
        while (!isAtEnd() && current().type == TokenType.BULLET_POINT) {
            items.add(parseListItem())
            skipNewlines()
        }
        
        return BulletList(items)
    }
    
    private fun parseOrderedList(): OrderedList {
        val items = mutableListOf<ListItem>()
        
        while (!isAtEnd() && current().type == TokenType.NUMBERED_POINT) {
            items.add(parseListItem())
            skipNewlines()
        }
        
        return OrderedList(items)
    }
    
    private fun parseListItem(): ListItem {
        // Skip the bullet point or number
        advance()
        skipWhitespace()
        
        val content = mutableListOf<MarkdownNode>()
        
        // Parse inline content until end of line
        while (!isAtEnd() && current().type != TokenType.NEWLINE && 
               current().type != TokenType.BULLET_POINT && 
               current().type != TokenType.NUMBERED_POINT) {
            val inline = parseInlineElement()
            if (inline != null) {
                content.add(inline)
            }
        }
        
        return ListItem(content)
    }
    
    private fun parseParagraph(): Paragraph {
        val content = mutableListOf<MarkdownNode>()
        
        while (!isAtEnd() && 
               current().type != TokenType.NEWLINE &&
               current().type != TokenType.HEADING_1 &&
               current().type != TokenType.HEADING_2 &&
               current().type != TokenType.HEADING_3 &&
               current().type != TokenType.HEADING_4 &&
               current().type != TokenType.HEADING_5 &&
               current().type != TokenType.BULLET_POINT &&
               current().type != TokenType.NUMBERED_POINT) {
            
            val inline = parseInlineElement()
            if (inline != null) {
                content.add(inline)
            }
        }
        
        return Paragraph(content)
    }
    
    private fun parseInlineElement(): MarkdownNode? {
        if (isAtEnd()) return null
        
        return when (current().type) {
            TokenType.BOLD -> parseBold()
            TokenType.ITALIC -> parseItalic()
            TokenType.STRIKETHROUGH -> parseStrikethrough()
            TokenType.TEXT -> parseText()
            TokenType.WHITESPACE -> {
                val space = current().value
                advance()
                Text(space)
            }
            else -> {
                advance() // Skip unknown token
                null
            }
        }
    }
    
    private fun parseBold(): Bold {
        advance() // Skip opening **
        val content = mutableListOf<MarkdownNode>()
        
        while (!isAtEnd() && current().type != TokenType.BOLD && current().type != TokenType.EOF) {
            val inline = parseInlineElement()
            if (inline != null) {
                content.add(inline)
            }
            if (position >= tokens.size) break // Safety check
        }
        
        if (!isAtEnd() && current().type == TokenType.BOLD) {
            advance() // Skip closing **
        }
        
        return Bold(content)
    }
    
    private fun parseItalic(): Italic {
        advance() // Skip opening *
        val content = mutableListOf<MarkdownNode>()
        
        while (!isAtEnd() && current().type != TokenType.ITALIC && current().type != TokenType.EOF) {
            val inline = parseInlineElement()
            if (inline != null) {
                content.add(inline)
            }
            if (position >= tokens.size) break // Safety check
        }
        
        if (!isAtEnd() && current().type == TokenType.ITALIC) {
            advance() // Skip closing *
        }
        
        return Italic(content)
    }
    
    private fun parseStrikethrough(): Strikethrough {
        advance() // Skip opening ~~
        val content = mutableListOf<MarkdownNode>()
        
        while (!isAtEnd() && current().type != TokenType.STRIKETHROUGH && current().type != TokenType.EOF) {
            val inline = parseInlineElement()
            if (inline != null) {
                content.add(inline)
            }
            if (position >= tokens.size) break // Safety check
        }
        
        if (!isAtEnd() && current().type == TokenType.STRIKETHROUGH) {
            advance() // Skip closing ~~
        }
        
        return Strikethrough(content)
    }
    
    private fun parseText(): Text {
        val text = current().value
        advance()
        return Text(text)
    }
    
    private fun skipWhitespace() {
        while (!isAtEnd() && current().type == TokenType.WHITESPACE) {
            advance()
        }
    }
    
    private fun skipNewlines() {
        while (!isAtEnd() && current().type == TokenType.NEWLINE) {
            advance()
        }
    }
    
    private fun current(): Token {
        if (isAtEnd()) return Token(TokenType.EOF, "", 0, 0)
        return tokens[position]
    }
    
    private fun advance(): Token {
        if (!isAtEnd()) position++
        return previous()
    }
    
    private fun previous(): Token {
        return tokens[position - 1]
    }
    
    private fun isAtEnd(): Boolean {
        return position >= tokens.size
    }
}