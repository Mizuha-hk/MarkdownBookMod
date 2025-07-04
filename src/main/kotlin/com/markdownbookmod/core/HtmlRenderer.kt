package com.markdownbookmod.core

import com.markdownbookmod.core.interfaces.MarkdownVisitor
import com.markdownbookmod.core.models.*

/**
 * HTML renderer for markdown AST
 */
class HtmlRenderer : MarkdownVisitor {
    
    override fun visitDocument(node: Document): String {
        return node.children.joinToString("\n") { it.accept(this) }
    }
    
    override fun visitHeading(node: Heading): String {
        return "<h${node.level}>${escapeHtml(node.text)}</h${node.level}>"
    }
    
    override fun visitParagraph(node: Paragraph): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return if (content.isNotBlank()) "<p>$content</p>" else ""
    }
    
    override fun visitText(node: Text): String {
        return escapeHtml(node.content)
    }
    
    override fun visitBold(node: Bold): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return "<strong>$content</strong>"
    }
    
    override fun visitItalic(node: Italic): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return "<em>$content</em>"
    }
    
    override fun visitStrikethrough(node: Strikethrough): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return "<del>$content</del>"
    }
    
    override fun visitBulletList(node: BulletList): String {
        val items = node.items.joinToString("\n") { it.accept(this) }
        return "<ul>\n$items\n</ul>"
    }
    
    override fun visitOrderedList(node: OrderedList): String {
        val items = node.items.joinToString("\n") { it.accept(this) }
        return "<ol>\n$items\n</ol>"
    }
    
    override fun visitListItem(node: ListItem): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return "<li>$content</li>"
    }
    
    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}