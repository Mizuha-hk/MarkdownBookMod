package com.markdownbookmod.core.interfaces

import com.markdownbookmod.core.models.*

/**
 * Visitor interface for rendering markdown AST nodes
 */
interface MarkdownVisitor {
    fun visitDocument(node: Document): String
    fun visitHeading(node: Heading): String
    fun visitParagraph(node: Paragraph): String
    fun visitText(node: Text): String
    fun visitBold(node: Bold): String
    fun visitItalic(node: Italic): String
    fun visitStrikethrough(node: Strikethrough): String
    fun visitBulletList(node: BulletList): String
    fun visitOrderedList(node: OrderedList): String
    fun visitListItem(node: ListItem): String
}