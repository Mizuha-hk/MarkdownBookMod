package com.markdownbookmod.core.models

import com.markdownbookmod.core.interfaces.MarkdownVisitor

/**
 * ATSの基本ノードを表す抽象クラス
 */
abstract class MarkdownNode {
    abstract fun accept(visitor: MarkdownVisitor): String
}