package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownのドキュメントを表すクラス
 */
class Document(val children: List<MarkdownNode>): MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String {
        return visitor.visitDocument(this)
    }
}