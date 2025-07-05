package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownの順序付きリストを表すクラス
 */
class OrderedList(val items: List<ListItem>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String = visitor.visitOrderedList(this)
}