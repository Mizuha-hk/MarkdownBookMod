package com.markdownbookmod.data.lang

class JajpLangGenerator(packOutput: net.minecraft.data.PackOutput) : net.neoforged.neoforge.common.data.LanguageProvider(packOutput, com.markdownbookmod.Markdownbookmod.ID, "ja_jp") {
    override fun addTranslations() {
        add(com.markdownbookmod.item.ModItems.MARKDOWNBOOK.get(), "マークダウンブック")
        add("string.markdown_book.title", "マークダウンブック編集")
        add("string.markdown_book.view_title", "マークダウンブック表示")
    }
}