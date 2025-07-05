package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownの箇条書きを表すクラス
 */
class BulletList(val items: List<ListItem>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String {
        return visitor.visitBulletList(this)
    }
}