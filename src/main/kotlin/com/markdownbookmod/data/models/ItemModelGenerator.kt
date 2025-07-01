package com.markdownbookmod.data.models

import com.markdownbookmod.item.ModItems
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ItemModelOutput
import net.minecraft.client.data.models.model.ModelInstance
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.resources.ResourceLocation
import java.util.function.BiConsumer

class ItemModelGenerator(output: ItemModelOutput, modelOutput: BiConsumer<ResourceLocation, ModelInstance>): ItemModelGenerators(output, modelOutput) {
    override fun run() {
        generateFlatItem(ModItems.MARKDOWNBOOK.get(), ModelTemplates.FLAT_ITEM)
    }
}