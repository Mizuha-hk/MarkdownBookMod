package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownのテキストノードを表すクラス
 */
class Text(val content: String) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String {
        return visitor.visitText(this)
    }
}