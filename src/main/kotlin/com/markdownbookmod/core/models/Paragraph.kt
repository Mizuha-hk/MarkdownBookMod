package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownの段落を表すクラス
 */
class Paragraph(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String {
        return visitor.visitParagraph(this)
    }
}