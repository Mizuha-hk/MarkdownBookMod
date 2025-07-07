package com.markdownbookmod.parser

/**
 * Parser for Markdown tokens.
 * Converts tokens from the lexer into an Abstract Syntax Tree (AST).
 */
class MarkdownParser {
    
    /**
     * Represents different types of AST nodes
     */
    sealed class ASTNode {
        data class Document(val children: List<ASTNode>) : ASTNode()
        data class Header(val level: Int, val text: String) : ASTNode()
        data class Paragraph(val children: List<ASTNode>) : ASTNode()
        data class Bold(val text: String) : ASTNode()
        data class Italic(val text: String) : ASTNode()
        data class Strikethrough(val text: String) : ASTNode()
        data class InlineCode(val text: String) : ASTNode()
        data class CodeBlock(val text: String, val language: String = "") : ASTNode()
        data class Link(val text: String, val url: String) : ASTNode()
        data class Image(val altText: String, val url: String) : ASTNode()
        data class ListItem(val text: String) : ASTNode()
        data class UnorderedList(val items: List<ListItem>) : ASTNode()
        data class Blockquote(val text: String) : ASTNode()
        data class Text(val content: String) : ASTNode()
        object LineBreak : ASTNode()
    }
    
    private var tokens: List<MarkdownLexer.Token> = emptyList()
    private var position: Int = 0
    private var currentToken: MarkdownLexer.Token? = null
    
    /**
     * Parse tokens into an AST
     */
    fun parse(tokens: List<MarkdownLexer.Token>): ASTNode.Document {
        this.tokens = tokens
        this.position = 0
        this.currentToken = if (tokens.isNotEmpty()) tokens[0] else null
        
        val children = mutableListOf<ASTNode>()
        
        while (currentToken?.type != MarkdownLexer.TokenType.EOF) {
            val node = parseBlock()
            if (node != null) {
                children.add(node)
            }
        }
        
        return ASTNode.Document(children)
    }
    
    /**
     * Parse a block-level element
     */
    private fun parseBlock(): ASTNode? {
        return when (currentToken?.type) {
            MarkdownLexer.TokenType.HEADER -> parseHeader()
            MarkdownLexer.TokenType.CODE_BLOCK -> parseCodeBlock()
            MarkdownLexer.TokenType.BLOCKQUOTE -> parseBlockquote()
            MarkdownLexer.TokenType.LIST_ITEM -> parseList()
            MarkdownLexer.TokenType.LINE_BREAK -> {
                advance()
                ASTNode.LineBreak
            }
            else -> parseParagraph()
        }
    }
    
    /**
     * Parse a header element
     */
    private fun parseHeader(): ASTNode.Header {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract level and text from header content
        val level = content.takeWhile { it == '#' }.length
        val text = content.substringAfter("#".repeat(level)).trim()
        
        return ASTNode.Header(level, text)
    }
    
    /**
     * Parse a code block
     */
    private fun parseCodeBlock(): ASTNode.CodeBlock {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract code content from ```code```
        val codeContent = content.removeSurrounding("```", "```")
        val lines = codeContent.split('\n')
        val language = if (lines.isNotEmpty()) lines[0].trim() else ""
        val code = if (lines.size > 1) lines.drop(1).joinToString("\n") else codeContent
        
        return ASTNode.CodeBlock(code, language)
    }
    
    /**
     * Parse a blockquote
     */
    private fun parseBlockquote(): ASTNode.Blockquote {
        val content = currentToken?.content ?: ""
        advance()
        
        // Remove > prefix
        val text = content.removePrefix("> ").removePrefix(">")
        return ASTNode.Blockquote(text)
    }
    
    /**
     * Parse a list (collect consecutive list items)
     */
    private fun parseList(): ASTNode.UnorderedList {
        val items = mutableListOf<ASTNode.ListItem>()
        
        while (currentToken?.type == MarkdownLexer.TokenType.LIST_ITEM) {
            val content = currentToken?.content ?: ""
            val text = content.substringAfter(" ").trim()
            items.add(ASTNode.ListItem(text))
            advance()
            
            // Skip line breaks between list items
            if (currentToken?.type == MarkdownLexer.TokenType.LINE_BREAK) {
                advance()
            }
        }
        
        return ASTNode.UnorderedList(items)
    }
    
    /**
     * Parse a paragraph (collection of inline elements)
     */
    private fun parseParagraph(): ASTNode.Paragraph {
        val children = mutableListOf<ASTNode>()
        
        while (currentToken?.type != MarkdownLexer.TokenType.EOF &&
               currentToken?.type !in setOf(
                   MarkdownLexer.TokenType.HEADER,
                   MarkdownLexer.TokenType.CODE_BLOCK,
                   MarkdownLexer.TokenType.BLOCKQUOTE,
                   MarkdownLexer.TokenType.LIST_ITEM,
                   MarkdownLexer.TokenType.LINE_BREAK
               )) {
            
            val node = parseInline()
            if (node != null) {
                children.add(node)
            }
        }
        
        return ASTNode.Paragraph(children)
    }
    
    /**
     * Parse inline elements
     */
    private fun parseInline(): ASTNode? {
        return when (currentToken?.type) {
            MarkdownLexer.TokenType.BOLD -> parseBold()
            MarkdownLexer.TokenType.ITALIC -> parseItalic()
            MarkdownLexer.TokenType.STRIKETHROUGH -> parseStrikethrough()
            MarkdownLexer.TokenType.CODE_INLINE -> parseInlineCode()
            MarkdownLexer.TokenType.LINK -> parseLink()
            MarkdownLexer.TokenType.IMAGE -> parseImage()
            MarkdownLexer.TokenType.TEXT -> parseText()
            else -> {
                advance()
                null
            }
        }
    }
    
    /**
     * Parse bold text
     */
    private fun parseBold(): ASTNode.Bold {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract text from **text** or __text__
        val text = when {
            content.startsWith("**") && content.endsWith("**") -> 
                content.removeSurrounding("**", "**")
            content.startsWith("__") && content.endsWith("__") -> 
                content.removeSurrounding("__", "__")
            else -> content
        }
        
        return ASTNode.Bold(text)
    }
    
    /**
     * Parse italic text
     */
    private fun parseItalic(): ASTNode.Italic {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract text from *text* or _text_
        val text = when {
            content.startsWith("*") && content.endsWith("*") -> 
                content.removeSurrounding("*", "*")
            content.startsWith("_") && content.endsWith("_") -> 
                content.removeSurrounding("_", "_")
            else -> content
        }
        
        return ASTNode.Italic(text)
    }
    
    /**
     * Parse strikethrough text
     */
    private fun parseStrikethrough(): ASTNode.Strikethrough {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract text from ~~text~~
        val text = content.removeSurrounding("~~", "~~")
        return ASTNode.Strikethrough(text)
    }
    
    /**
     * Parse inline code
     */
    private fun parseInlineCode(): ASTNode.InlineCode {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract text from `text`
        val text = content.removeSurrounding("`", "`")
        return ASTNode.InlineCode(text)
    }
    
    /**
     * Parse link
     */
    private fun parseLink(): ASTNode.Link {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract text and URL from [text](url)
        val linkRegex = """\[([^\]]*)\]\(([^\)]*)\)""".toRegex()
        val matchResult = linkRegex.find(content)
        
        return if (matchResult != null) {
            val (text, url) = matchResult.destructured
            ASTNode.Link(text, url)
        } else {
            ASTNode.Text(content)
        }
    }
    
    /**
     * Parse image
     */
    private fun parseImage(): ASTNode.Image {
        val content = currentToken?.content ?: ""
        advance()
        
        // Extract alt text and URL from ![alt](url)
        val imageRegex = """!\[([^\]]*)\]\(([^\)]*)\)""".toRegex()
        val matchResult = imageRegex.find(content)
        
        return if (matchResult != null) {
            val (altText, url) = matchResult.destructured
            ASTNode.Image(altText, url)
        } else {
            ASTNode.Image("", "")
        }
    }
    
    /**
     * Parse plain text
     */
    private fun parseText(): ASTNode.Text {
        val content = currentToken?.content ?: ""
        advance()
        return ASTNode.Text(content)
    }
    
    /**
     * Advance to the next token
     */
    private fun advance() {
        position++
        currentToken = if (position < tokens.size) tokens[position] else null
    }
}