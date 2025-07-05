package com.markdownbookmod.core

import com.markdownbookmod.core.interfaces.MarkdownVisitor
import com.markdownbookmod.core.models.*
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.ChatFormatting

/**
 * Minecraft Component renderer for markdown AST
 */
class MinecraftComponentRenderer : MarkdownVisitor {
    
    override fun visitDocument(node: Document): String {
        return node.children.joinToString("\n") { it.accept(this) }
    }
    
    override fun visitHeading(node: Heading): String {
        // Use color and size to differentiate heading levels
        val color = when (node.level) {
            1 -> ChatFormatting.DARK_BLUE
            2 -> ChatFormatting.BLUE
            3 -> ChatFormatting.DARK_GREEN
            4 -> ChatFormatting.GREEN
            5 -> ChatFormatting.DARK_PURPLE
            else -> ChatFormatting.BLACK
        }
        return "${color}${node.text}${ChatFormatting.RESET}"
    }
    
    override fun visitParagraph(node: Paragraph): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return if (content.isNotBlank()) content else ""
    }
    
    override fun visitText(node: Text): String {
        return node.content
    }
    
    override fun visitBold(node: Bold): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return "${ChatFormatting.BOLD}$content${ChatFormatting.RESET}"
    }
    
    override fun visitItalic(node: Italic): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return "${ChatFormatting.ITALIC}$content${ChatFormatting.RESET}"
    }
    
    override fun visitStrikethrough(node: Strikethrough): String {
        val content = node.children.joinToString("") { it.accept(this) }
        return "${ChatFormatting.STRIKETHROUGH}$content${ChatFormatting.RESET}"
    }
    
    override fun visitBulletList(node: BulletList): String {
        val items = node.items.joinToString("\n") { "• ${it.accept(this)}" }
        return items
    }
    
    override fun visitOrderedList(node: OrderedList): String {
        val items = node.items.mapIndexed { index, item -> 
            "${index + 1}. ${item.accept(this)}" 
        }.joinToString("\n")
        return items
    }
    
    override fun visitListItem(node: ListItem): String {
        return node.children.joinToString("") { it.accept(this) }
    }
}