package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownの太字を表すクラス
 */
class Bold(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String {
        return visitor.visitBold(this)
    }
}