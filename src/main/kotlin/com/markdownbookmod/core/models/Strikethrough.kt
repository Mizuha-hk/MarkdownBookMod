package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * Markdownの取り消し線を表すクラス
 */
class Strikethrough(val children: List<MarkdownNode>) : MarkdownNode() {
    override fun accept(visitor: MarkdownVisitor): String {
      return visitor.visitStrikethrough(this)
    }
}