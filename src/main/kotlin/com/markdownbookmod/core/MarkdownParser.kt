package com.markdownbookmod.core

/**
 * AST (Abstract Syntax Tree) nodes for parsed markdown structure
 */
sealed class MarkdownNode {
    data class Document(val children: List<MarkdownNode>) : MarkdownNode()
    data class Header(val level: Int, val text: String) : MarkdownNode()
    data class Paragraph(val children: List<MarkdownNode>) : MarkdownNode()
    data class Bold(val text: String) : MarkdownNode()
    data class Italic(val text: String) : MarkdownNode()
    data class Strikethrough(val text: String) : MarkdownNode()
    data class CodeInline(val code: String) : MarkdownNode()
    data class CodeBlock(val language: String, val code: String) : MarkdownNode()
    data class Link(val text: String, val url: String) : MarkdownNode()
    data class Image(val altText: String, val url: String) : MarkdownNode()
    data class UnorderedList(val items: List<ListItem>) : MarkdownNode()
    data class OrderedList(val items: List<ListItem>) : MarkdownNode()
    data class ListItem(val content: String) : MarkdownNode()
    data class Blockquote(val content: String) : MarkdownNode()
    data class Text(val content: String) : MarkdownNode()
    object LineBreak : MarkdownNode()
}

/**
 * Parser for converting markdown tokens into an Abstract Syntax Tree
 */
class MarkdownParser {
    private lateinit var tokens: List<Token>
    private var position: Int = 0
    
    /**
     * Parse tokens into a markdown AST
     * @param tokenList List of tokens from the lexer
     * @return Root document node containing the parsed structure
     */
    fun parse(tokenList: List<Token>): MarkdownNode.Document {
        tokens = tokenList
        position = 0
        
        val children = mutableListOf<MarkdownNode>()
        
        while (!isAtEnd()) {
            val node = parseNode()
            if (node != null) {
                children.add(node)
            }
        }
        
        return MarkdownNode.Document(children)
    }
    
    private fun parseNode(): MarkdownNode? {
        return when (currentToken()?.type) {
            TokenType.HEADER -> parseHeader()
            TokenType.CODE_BLOCK -> parseCodeBlock()
            TokenType.LIST_UNORDERED -> parseUnorderedList()
            TokenType.LIST_ORDERED -> parseOrderedList()
            TokenType.BLOCKQUOTE -> parseBlockquote()
            TokenType.NEWLINE -> {
                advance()
                MarkdownNode.LineBreak
            }
            TokenType.WHITESPACE -> {
                advance()
                null // Skip whitespace at top level
            }
            TokenType.EOF -> null
            else -> parseParagraph()
        }
    }
    
    private fun parseHeader(): MarkdownNode.Header {
        val token = advance()
        val content = token.value
        val level = content.takeWhile { it == '#' }.length
        val text = content.substring(level).trim()
        return MarkdownNode.Header(level, text)
    }
    
    private fun parseCodeBlock(): MarkdownNode.CodeBlock {
        val token = advance()
        val content = token.value
        val lines = content.split('\n', limit = 2)
        val language = if (lines.size > 1) lines[0] else ""
        val code = if (lines.size > 1) lines[1] else content
        return MarkdownNode.CodeBlock(language, code)
    }
    
    private fun parseUnorderedList(): MarkdownNode.UnorderedList {
        val items = mutableListOf<MarkdownNode.ListItem>()
        
        while (!isAtEnd() && currentToken()?.type == TokenType.LIST_UNORDERED) {
            val token = advance()
            items.add(MarkdownNode.ListItem(token.value))
            
            // Skip newline after list item
            if (currentToken()?.type == TokenType.NEWLINE) {
                advance()
            }
        }
        
        return MarkdownNode.UnorderedList(items)
    }
    
    private fun parseOrderedList(): MarkdownNode.OrderedList {
        val items = mutableListOf<MarkdownNode.ListItem>()
        
        while (!isAtEnd() && currentToken()?.type == TokenType.LIST_ORDERED) {
            val token = advance()
            val content = token.value.split('|', limit = 2).getOrElse(1) { token.value }
            items.add(MarkdownNode.ListItem(content))
            
            // Skip newline after list item
            if (currentToken()?.type == TokenType.NEWLINE) {
                advance()
            }
        }
        
        return MarkdownNode.OrderedList(items)
    }
    
    private fun parseBlockquote(): MarkdownNode.Blockquote {
        val token = advance()
        return MarkdownNode.Blockquote(token.value)
    }
    
    private fun parseParagraph(): MarkdownNode.Paragraph {
        val children = mutableListOf<MarkdownNode>()
        
        while (!isAtEnd() && !isBlockElement()) {
            val node = parseInlineElement()
            if (node != null) {
                children.add(node)
            }
        }
        
        return MarkdownNode.Paragraph(children)
    }
    
    private fun parseInlineElement(): MarkdownNode? {
        return when (currentToken()?.type) {
            TokenType.BOLD -> {
                val token = advance()
                MarkdownNode.Bold(token.value)
            }
            TokenType.ITALIC -> {
                val token = advance()
                MarkdownNode.Italic(token.value)
            }
            TokenType.STRIKETHROUGH -> {
                val token = advance()
                MarkdownNode.Strikethrough(token.value)
            }
            TokenType.CODE_INLINE -> {
                val token = advance()
                MarkdownNode.CodeInline(token.value)
            }
            TokenType.LINK -> {
                val token = advance()
                val parts = token.value.split('|', limit = 2)
                val text = parts.getOrElse(0) { "" }
                val url = parts.getOrElse(1) { "" }
                MarkdownNode.Link(text, url)
            }
            TokenType.IMAGE -> {
                val token = advance()
                val parts = token.value.split('|', limit = 2)
                val altText = parts.getOrElse(0) { "" }
                val url = parts.getOrElse(1) { "" }
                MarkdownNode.Image(altText, url)
            }
            TokenType.TEXT -> {
                val token = advance()
                MarkdownNode.Text(token.value)
            }
            TokenType.WHITESPACE -> {
                val token = advance()
                MarkdownNode.Text(token.value)
            }
            TokenType.NEWLINE -> {
                advance()
                null // End of paragraph
            }
            else -> {
                advance()
                null
            }
        }
    }
    
    private fun isBlockElement(): Boolean {
        return when (currentToken()?.type) {
            TokenType.HEADER,
            TokenType.CODE_BLOCK,
            TokenType.LIST_UNORDERED,
            TokenType.LIST_ORDERED,
            TokenType.BLOCKQUOTE,
            TokenType.NEWLINE,
            TokenType.EOF -> true
            else -> false
        }
    }
    
    private fun currentToken(): Token? {
        return if (position < tokens.size) tokens[position] else null
    }
    
    private fun advance(): Token {
        val token = currentToken() ?: Token(TokenType.EOF, "", -1)
        if (!isAtEnd()) {
            position++
        }
        return token
    }
    
    private fun isAtEnd(): Boolean {
        return position >= tokens.size || currentToken()?.type == TokenType.EOF
    }
}