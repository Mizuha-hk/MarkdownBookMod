package com.markdownbookmod.parser

/**
 * Lexical analyzer for Markdown text.
 * Converts raw markdown text into tokens for parsing.
 */
class MarkdownLexer {
    
    /**
     * Represents different types of markdown tokens
     */
    enum class TokenType {
        HEADER,
        BOLD,
        ITALIC,
        STRIKETHROUGH,
        CODE_INLINE,
        CODE_BLOCK,
        LINK,
        IMAGE,
        LIST_ITEM,
        BLOCKQUOTE,
        LINE_BREAK,
        TEXT,
        EOF
    }
    
    /**
     * Data class representing a token
     */
    data class Token(
        val type: TokenType,
        val content: String,
        val position: Int = 0
    )
    
    private var input: String = ""
    private var position: Int = 0
    private var currentChar: Char? = null
    
    /**
     * Initialize the lexer with input text
     */
    fun initialize(text: String) {
        this.input = text
        this.position = 0
        this.currentChar = if (input.isNotEmpty()) input[0] else null
    }
    
    /**
     * Advance to the next character
     */
    private fun advance() {
        position++
        currentChar = if (position >= input.length) null else input[position]
    }
    
    /**
     * Peek at the next character without advancing
     */
    private fun peek(): Char? {
        val peekPos = position + 1
        return if (peekPos >= input.length) null else input[peekPos]
    }
    
    /**
     * Get the next token from the input
     */
    fun nextToken(): Token {
        while (currentChar != null) {
            when (currentChar) {
                '#' -> return parseHeader()
                '*' -> return parseAsterisk()
                '_' -> return parseUnderscore()
                '~' -> return parseStrikethrough()
                '`' -> return parseCode()
                '[' -> return parseLink()
                '!' -> return parseImage()
                '-', '+' -> return parseListItem()
                '>' -> return parseBlockquote()
                '\n' -> return parseLineBreak()
                else -> return parseText()
            }
        }
        return Token(TokenType.EOF, "")
    }
    
    private fun parseHeader(): Token {
        val start = position
        var level = 0
        
        while (currentChar == '#') {
            level++
            advance()
        }
        
        // Skip space after #
        if (currentChar == ' ') {
            advance()
        }
        
        // Read header text until newline
        val content = StringBuilder()
        while (currentChar != null && currentChar != '\n') {
            content.append(currentChar)
            advance()
        }
        
        return Token(TokenType.HEADER, "#".repeat(level) + " " + content.toString(), start)
    }
    
    private fun parseAsterisk(): Token {
        val start = position
        if (peek() == '*') {
            // Bold text **
            advance()
            advance()
            val content = readUntil("**")
            return Token(TokenType.BOLD, "**$content**", start)
        } else {
            // Italic text *
            advance()
            val content = readUntil("*")
            return Token(TokenType.ITALIC, "*$content*", start)
        }
    }
    
    private fun parseUnderscore(): Token {
        val start = position
        if (peek() == '_') {
            // Bold text __
            advance()
            advance()
            val content = readUntil("__")
            return Token(TokenType.BOLD, "__${content}__", start)
        } else {
            // Italic text _
            advance()
            val content = readUntil("_")
            return Token(TokenType.ITALIC, "_${content}_", start)
        }
    }
    
    private fun parseStrikethrough(): Token {
        val start = position
        if (peek() == '~') {
            advance()
            advance()
            val content = readUntil("~~")
            return Token(TokenType.STRIKETHROUGH, "~~$content~~", start)
        } else {
            return parseText()
        }
    }
    
    private fun parseCode(): Token {
        val start = position
        if (peek() == '`' && position + 2 < input.length && input[position + 2] == '`') {
            // Code block ```
            advance()
            advance()
            advance()
            val content = readUntil("```")
            return Token(TokenType.CODE_BLOCK, "```$content```", start)
        } else {
            // Inline code `
            advance()
            val content = readUntil("`")
            return Token(TokenType.CODE_INLINE, "`$content`", start)
        }
    }
    
    private fun parseLink(): Token {
        val start = position
        advance() // Skip [
        val linkText = readUntil("]")
        if (currentChar == ']') {
            advance()
            if (currentChar == '(') {
                advance()
                val url = readUntil(")")
                return Token(TokenType.LINK, "[$linkText]($url)", start)
            }
        }
        return parseText()
    }
    
    private fun parseImage(): Token {
        val start = position
        if (peek() == '[') {
            advance() // Skip !
            advance() // Skip [
            val altText = readUntil("]")
            if (currentChar == ']') {
                advance()
                if (currentChar == '(') {
                    advance()
                    val url = readUntil(")")
                    return Token(TokenType.IMAGE, "![$altText]($url)", start)
                }
            }
        }
        return parseText()
    }
    
    private fun parseListItem(): Token {
        val start = position
        val marker = currentChar
        advance()
        if (currentChar == ' ') {
            advance()
            val content = StringBuilder()
            content.append(marker).append(" ")
            while (currentChar != null && currentChar != '\n') {
                content.append(currentChar)
                advance()
            }
            return Token(TokenType.LIST_ITEM, content.toString(), start)
        }
        return parseText()
    }
    
    private fun parseBlockquote(): Token {
        val start = position
        advance() // Skip >
        if (currentChar == ' ') {
            advance()
        }
        val content = StringBuilder("> ")
        while (currentChar != null && currentChar != '\n') {
            content.append(currentChar)
            advance()
        }
        return Token(TokenType.BLOCKQUOTE, content.toString(), start)
    }
    
    private fun parseLineBreak(): Token {
        val start = position
        advance()
        return Token(TokenType.LINE_BREAK, "\n", start)
    }
    
    private fun parseText(): Token {
        val start = position
        val content = StringBuilder()
        
        while (currentChar != null && 
               currentChar !in setOf('#', '*', '_', '~', '`', '[', '!', '-', '+', '>', '\n')) {
            content.append(currentChar)
            advance()
        }
        
        return Token(TokenType.TEXT, content.toString(), start)
    }
    
    private fun readUntil(delimiter: String): String {
        val content = StringBuilder()
        
        while (currentChar != null) {
            if (input.substring(position).startsWith(delimiter)) {
                // Skip the delimiter
                repeat(delimiter.length) { advance() }
                break
            }
            content.append(currentChar)
            advance()
        }
        
        return content.toString()
    }
    
    /**
     * Tokenize the entire input and return all tokens
     */
    fun tokenize(text: String): List<Token> {
        initialize(text)
        val tokens = mutableListOf<Token>()
        
        do {
            val token = nextToken()
            tokens.add(token)
        } while (token.type != TokenType.EOF)
        
        return tokens
    }
}