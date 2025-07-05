package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * リストアイテムを表すクラス
 */
class ListItem(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitListItem(this)
}