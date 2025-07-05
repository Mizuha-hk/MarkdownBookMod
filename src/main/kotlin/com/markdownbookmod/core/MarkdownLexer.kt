package com.markdownbookmod.core

/**
 * 軸解析器クラス
 */
class MarkdownLexer(private val input: String) {
    private var position = 0
    private var line = 1
    private var column = 1
    
    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        
        while (position < input.length) {
            val token = nextToken()
            if (token != null) {
                tokens.add(token)
            }
        }
        
        tokens.add(Token(TokenType.EOF, "", position, line))
        return tokens
    }
    
    private fun nextToken(): Token? {
        if (position >= input.length) return null
        
        val startPosition = position
        val startLine = line
        
        val char = input[position]
        
        return when {
            char == '\n' -> {
                position++
                line++
                column = 1
                Token(TokenType.NEWLINE, "\n", startPosition, startLine)
            }
            char.isWhitespace() -> {
                val whitespace = consumeWhile { it.isWhitespace() && it != '\n' }
                Token(TokenType.WHITESPACE, whitespace, startPosition, startLine)
            }
            char == '#' && isStartOfLine() -> parseHeading()
            char == '*' -> parseAsterisk()
            char == '~' && peek() == '~' -> parseStrikethrough()
            char == '-' && isStartOfLine() -> parseBulletOrText()
            char.isDigit() && isStartOfLine() -> parseNumberedListOrText()
            else -> parseText()
        }
    }
    
    private fun parseHeading(): Token {
        val startPosition = position
        val startLine = line
        var level = 0
        
        while (position < input.length && input[position] == '#' && level < 5) {
            position++
            column++
            level++
        }
        
        // Check for space after #
        if (position < input.length && input[position] == ' ') {
            val tokenType = when (level) {
                1 -> TokenType.HEADING_1
                2 -> TokenType.HEADING_2
                3 -> TokenType.HEADING_3
                4 -> TokenType.HEADING_4
                5 -> TokenType.HEADING_5
                else -> TokenType.TEXT
            }
            return Token(tokenType, "#".repeat(level), startPosition, startLine)
        } else {
            // Not a heading, treat as text
            position = startPosition
            return parseText()
        }
    }
    
    private fun parseAsterisk(): Token {
        val startPosition = position
        val startLine = line
        
        if (peek() == '*') {
            // Bold **text**
            position += 2
            column += 2
            return Token(TokenType.BOLD, "**", startPosition, startLine)
        } else {
            // Italic *text* or bullet point
            if (isStartOfLine()) {
                // Check if it's a bullet point
                if (position + 1 < input.length && input[position + 1] == ' ') {
                    position++
                    column++
                    return Token(TokenType.BULLET_POINT, "*", startPosition, startLine)
                }
            }
            // Italic
            position++
            column++
            return Token(TokenType.ITALIC, "*", startPosition, startLine)
        }
    }
    
    private fun parseStrikethrough(): Token {
        val startPosition = position
        val startLine = line
        position += 2
        column += 2
        return Token(TokenType.STRIKETHROUGH, "~~", startPosition, startLine)
    }
    
    private fun parseBulletOrText(): Token {
        val startPosition = position
        val startLine = line
        
        if (position + 1 < input.length && input[position + 1] == ' ') {
            position++
            column++
            return Token(TokenType.BULLET_POINT, "-", startPosition, startLine)
        } else {
            return parseText()
        }
    }
    
    private fun parseNumberedListOrText(): Token {
        val startPosition = position
        val startLine = line
        val savedPosition = position
        val savedColumn = column
        
        // Try to parse number followed by . and space
        val number = consumeWhile { it.isDigit() }
        if (position < input.length && input[position] == '.' && 
            position + 1 < input.length && input[position + 1] == ' ') {
            position += 2
            column += 2
            return Token(TokenType.NUMBERED_POINT, "$number.", startPosition, startLine)
        } else {
            // Restore position and parse as text
            position = savedPosition
            column = savedColumn
            return parseText()
        }
    }
    
    private fun parseText(): Token {
        val startPosition = position
        val startLine = line
        val text = consumeWhile { 
            it != '\n' && it != '*' && it != '~' && it != '#' && 
            (it != '-' || !isStartOfLine()) &&
            (!it.isDigit() || !isStartOfLine())
        }
        return Token(TokenType.TEXT, text, startPosition, startLine)
    }
    
    private fun consumeWhile(predicate: (Char) -> Boolean): String {
        val start = position
        while (position < input.length && predicate(input[position])) {
            if (input[position] == '\n') {
                line++
                column = 1
            } else {
                column++
            }
            position++
        }
        return input.substring(start, position)
    }
    
    private fun peek(): Char? {
        return if (position + 1 < input.length) input[position + 1] else null
    }
    
    private fun isStartOfLine(): Boolean {
        if (position == 0) return true
        var i = position - 1
        while (i >= 0 && input[i] != '\n') {
            if (!input[i].isWhitespace()) return false
            i--
        }
        return true
    }
}