package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownの見出しを表すクラス
 */
class Heading(val level: Int, val text: String) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String {
        return visitor.visitHeading(this)
    }
}
