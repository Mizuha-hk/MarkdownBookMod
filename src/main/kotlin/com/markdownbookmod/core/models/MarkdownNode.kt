package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Base class for all AST nodes
 */
abstract class MarkdownNode {
    abstract fun accept(visitor: MarkdownVisitor): String
}

/**
 * Represents a document containing multiple elements
 */
class Document(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitDocument(this)
}

/**
 * Represents a heading with level 1-5
 */
class Heading(val level: Int, val text: String) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitHeading(this)
}

/**
 * Represents a paragraph of text
 */
class Paragraph(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitParagraph(this)
}

/**
 * Represents plain text
 */
class Text(val content: String) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitText(this)
}

/**
 * Represents bold text
 */
class Bold(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitBold(this)
}

/**
 * Represents italic text
 */
class Italic(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitItalic(this)
}

/**
 * Represents strikethrough text
 */
class Strikethrough(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitStrikethrough(this)
}

/**
 * Represents an unordered (bullet) list
 */
class BulletList(val items: List<ListItem>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitBulletList(this)
}

/**
 * Represents an ordered (numbered) list
 */
class OrderedList(val items: List<ListItem>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitOrderedList(this)
}

/**
 * Represents a list item
 */
class ListItem(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitListItem(this)
}