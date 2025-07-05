package com.markdownbookmod.core

/**
 * Represents different types of tokens in markdown
 */
enum class TokenType {
    // Text content
    TEXT,
    WHITESPACE,
    NEWLINE,
    
    // Headings
    HEADING_1,
    HEADING_2,
    HEADING_3,
    HEADING_4,
    HEADING_5,
    
    // Text formatting
    BOLD,
    ITALIC,
    STRIKETHROUGH,
    
    // Lists
    BULLET_POINT,
    NUMBERED_POINT,
    
    // Special
    EOF
}

/**
 * Represents a token with its type, value, and position
 */
data class Token(
    val type: TokenType,
    val value: String,
    val position: Int = 0,
    val line: Int = 1
)