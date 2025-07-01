package com.markdownbookmod.data.lang

import com.markdownbookmod.Markdownbookmod
import com.markdownbookmod.item.ModItems
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class EnusLangGenerator(packOutput: PackOutput): LanguageProvider(packOutput, Markdownbookmod.ID, "en_us") {
    override fun addTranslations() {
        add(ModItems.MARKDOWNBOOK.get(), "Markdown Book")
    }
}