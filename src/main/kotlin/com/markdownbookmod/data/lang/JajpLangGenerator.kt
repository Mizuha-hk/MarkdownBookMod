package com.markdownbookmod.data.lang

class JajpLangGenerator(packOutput: net.minecraft.data.PackOutput) : net.neoforged.neoforge.common.data.LanguageProvider(packOutput, com.markdownbookmod.Markdownbookmod.ID, "ja_jp") {
    override fun addTranslations() {
        add(com.markdownbookmod.item.ModItems.MARKDOWNBOOK.get(), "マークダウンブック")
    }
}