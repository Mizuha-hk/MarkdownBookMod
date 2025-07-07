package com.markdownbookmod.core

/**
 * Token types for markdown lexical analysis
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
    LIST_UNORDERED,
    LIST_ORDERED,
    BLOCKQUOTE,
    TEXT,
    NEWLINE,
    WHITESPACE,
    EOF
}

/**
 * Token representing a lexical unit in markdown text
 */
data class Token(
    val type: TokenType,
    val value: String,
    val position: Int = 0
)

/**
 * Lexical analyzer for Markdown text
 * Converts raw markdown text into a sequence of tokens
 */
class MarkdownLexer {
    private var text: String = ""
    private var position: Int = 0
    private val tokens = mutableListOf<Token>()
    
    /**
     * Tokenize the given markdown text
     * @param input The markdown text to tokenize
     * @return List of tokens representing the lexical structure
     */
    fun tokenize(input: String): List<Token> {
        text = input
        position = 0
        tokens.clear()
        
        while (position < text.length) {
            when {
                isAtHeader() -> tokenizeHeader()
                isAtBold() -> tokenizeBold()
                isAtItalic() -> tokenizeItalic()
                isAtStrikethrough() -> tokenizeStrikethrough()
                isAtCodeInline() -> tokenizeCodeInline()
                isAtCodeBlock() -> tokenizeCodeBlock()
                isAtLink() -> tokenizeLink()
                isAtImage() -> tokenizeImage()
                isAtUnorderedList() -> tokenizeUnorderedList()
                isAtOrderedList() -> tokenizeOrderedList()
                isAtBlockquote() -> tokenizeBlockquote()
                isAtNewline() -> tokenizeNewline()
                isAtWhitespace() -> tokenizeWhitespace()
                else -> tokenizeText()
            }
        }
        
        tokens.add(Token(TokenType.EOF, "", position))
        return tokens.toList()
    }
    
    private fun isAtHeader(): Boolean {
        return position < text.length && text[position] == '#'
    }
    
    private fun isAtBold(): Boolean {
        return position + 1 < text.length && text.substring(position, position + 2) == "**"
    }
    
    private fun isAtItalic(): Boolean {
        return position < text.length && text[position] == '*' && !isAtBold()
    }
    
    private fun isAtStrikethrough(): Boolean {
        return position + 1 < text.length && text.substring(position, position + 2) == "~~"
    }
    
    private fun isAtCodeInline(): Boolean {
        return position < text.length && text[position] == '`'
    }
    
    private fun isAtCodeBlock(): Boolean {
        return position + 2 < text.length && text.substring(position, position + 3) == "```"
    }
    
    private fun isAtLink(): Boolean {
        return position < text.length && text[position] == '['
    }
    
    private fun isAtImage(): Boolean {
        return position + 1 < text.length && text.substring(position, position + 2) == "!["
    }
    
    private fun isAtUnorderedList(): Boolean {
        return (position == 0 || text[position - 1] == '\n') && 
               position < text.length && (text[position] == '-' || text[position] == '+') &&
               position + 1 < text.length && text[position + 1] == ' '
    }
    
    private fun isAtOrderedList(): Boolean {
        if (position > 0 && text[position - 1] != '\n') return false
        val start = position
        while (position < text.length && text[position].isDigit()) position++
        val hasDigits = position > start
        val hasDot = position < text.length && text[position] == '.'
        val hasSpace = position + 1 < text.length && text[position + 1] == ' '
        position = start
        return hasDigits && hasDot && hasSpace
    }
    
    private fun isAtBlockquote(): Boolean {
        return (position == 0 || text[position - 1] == '\n') && 
               position < text.length && text[position] == '>' &&
               position + 1 < text.length && text[position + 1] == ' '
    }
    
    private fun isAtNewline(): Boolean {
        return position < text.length && text[position] == '\n'
    }
    
    private fun isAtWhitespace(): Boolean {
        return position < text.length && text[position].isWhitespace() && text[position] != '\n'
    }
    
    private fun tokenizeHeader() {
        val start = position
        var level = 0
        while (position < text.length && text[position] == '#' && level < 6) {
            level++
            position++
        }
        if (position < text.length && text[position] == ' ') {
            position++
            val contentStart = position
            while (position < text.length && text[position] != '\n') {
                position++
            }
            val content = text.substring(contentStart, position)
            tokens.add(Token(TokenType.HEADER, "#".repeat(level) + " " + content, start))
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeBold() {
        val start = position
        position += 2 // Skip **
        val contentStart = position
        while (position + 1 < text.length && text.substring(position, position + 2) != "**") {
            position++
        }
        if (position + 1 < text.length) {
            val content = text.substring(contentStart, position)
            position += 2 // Skip closing **
            tokens.add(Token(TokenType.BOLD, content, start))
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeItalic() {
        val start = position
        position++ // Skip *
        val contentStart = position
        while (position < text.length && text[position] != '*') {
            position++
        }
        if (position < text.length) {
            val content = text.substring(contentStart, position)
            position++ // Skip closing *
            tokens.add(Token(TokenType.ITALIC, content, start))
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeStrikethrough() {
        val start = position
        position += 2 // Skip ~~
        val contentStart = position
        while (position + 1 < text.length && text.substring(position, position + 2) != "~~") {
            position++
        }
        if (position + 1 < text.length) {
            val content = text.substring(contentStart, position)
            position += 2 // Skip closing ~~
            tokens.add(Token(TokenType.STRIKETHROUGH, content, start))
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeCodeInline() {
        val start = position
        position++ // Skip `
        val contentStart = position
        while (position < text.length && text[position] != '`') {
            position++
        }
        if (position < text.length) {
            val content = text.substring(contentStart, position)
            position++ // Skip closing `
            tokens.add(Token(TokenType.CODE_INLINE, content, start))
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeCodeBlock() {
        val start = position
        position += 3 // Skip ```
        val languageStart = position
        while (position < text.length && text[position] != '\n') {
            position++
        }
        val language = text.substring(languageStart, position)
        if (position < text.length) position++ // Skip newline
        
        val contentStart = position
        while (position + 2 < text.length && text.substring(position, position + 3) != "```") {
            position++
        }
        if (position + 2 < text.length) {
            val content = text.substring(contentStart, position)
            position += 3 // Skip closing ```
            tokens.add(Token(TokenType.CODE_BLOCK, if (language.isNotEmpty()) "$language\n$content" else content, start))
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeLink() {
        val start = position
        position++ // Skip [
        val textStart = position
        while (position < text.length && text[position] != ']') {
            position++
        }
        if (position < text.length && position + 1 < text.length && text[position + 1] == '(') {
            val linkText = text.substring(textStart, position)
            position += 2 // Skip ](
            val urlStart = position
            while (position < text.length && text[position] != ')') {
                position++
            }
            if (position < text.length) {
                val url = text.substring(urlStart, position)
                position++ // Skip )
                tokens.add(Token(TokenType.LINK, "$linkText|$url", start))
            } else {
                position = start
                tokenizeText()
            }
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeImage() {
        val start = position
        position += 2 // Skip ![
        val altStart = position
        while (position < text.length && text[position] != ']') {
            position++
        }
        if (position < text.length && position + 1 < text.length && text[position + 1] == '(') {
            val altText = text.substring(altStart, position)
            position += 2 // Skip ](
            val urlStart = position
            while (position < text.length && text[position] != ')') {
                position++
            }
            if (position < text.length) {
                val url = text.substring(urlStart, position)
                position++ // Skip )
                tokens.add(Token(TokenType.IMAGE, "$altText|$url", start))
            } else {
                position = start
                tokenizeText()
            }
        } else {
            position = start
            tokenizeText()
        }
    }
    
    private fun tokenizeUnorderedList() {
        val start = position
        val marker = text[position]
        position += 2 // Skip marker and space
        val contentStart = position
        while (position < text.length && text[position] != '\n') {
            position++
        }
        val content = text.substring(contentStart, position)
        tokens.add(Token(TokenType.LIST_UNORDERED, content, start))
    }
    
    private fun tokenizeOrderedList() {
        val start = position
        val numberStart = position
        while (position < text.length && text[position].isDigit()) {
            position++
        }
        val number = text.substring(numberStart, position)
        position += 2 // Skip . and space
        val contentStart = position
        while (position < text.length && text[position] != '\n') {
            position++
        }
        val content = text.substring(contentStart, position)
        tokens.add(Token(TokenType.LIST_ORDERED, "$number|$content", start))
    }
    
    private fun tokenizeBlockquote() {
        val start = position
        position += 2 // Skip > and space
        val contentStart = position
        while (position < text.length && text[position] != '\n') {
            position++
        }
        val content = text.substring(contentStart, position)
        tokens.add(Token(TokenType.BLOCKQUOTE, content, start))
    }
    
    private fun tokenizeNewline() {
        val start = position
        position++
        tokens.add(Token(TokenType.NEWLINE, "\n", start))
    }
    
    private fun tokenizeWhitespace() {
        val start = position
        val whitespaceStart = position
        while (position < text.length && text[position].isWhitespace() && text[position] != '\n') {
            position++
        }
        val whitespace = text.substring(whitespaceStart, position)
        tokens.add(Token(TokenType.WHITESPACE, whitespace, start))
    }
    
    private fun tokenizeText() {
        val start = position
        val textStart = position
        while (position < text.length && !isSpecialCharacter()) {
            position++
        }
        val content = text.substring(textStart, position)
        if (content.isNotEmpty()) {
            tokens.add(Token(TokenType.TEXT, content, start))
        }
    }
    
    private fun isSpecialCharacter(): Boolean {
        return when {
            isAtHeader() -> true
            isAtBold() -> true
            isAtItalic() -> true
            isAtStrikethrough() -> true
            isAtCodeInline() -> true
            isAtCodeBlock() -> true
            isAtLink() -> true
            isAtImage() -> true
            isAtUnorderedList() -> true
            isAtOrderedList() -> true
            isAtBlockquote() -> true
            isAtNewline() -> true
            isAtWhitespace() -> true
            else -> false
        }
    }
}