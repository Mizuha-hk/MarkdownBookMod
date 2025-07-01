package com.markdownbookmod.data.models

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelOutput
import net.minecraft.client.data.models.blockstates.BlockStateGenerator
import net.minecraft.client.data.models.model.ModelInstance
import net.minecraft.resources.ResourceLocation
import java.util.function.BiConsumer
import java.util.function.Consumer

class BlockModelGenerator(stateOutput: Consumer<BlockStateGenerator>, itemOutput: ItemModelOutput, modelOutput: BiConsumer<ResourceLocation, ModelInstance>):BlockModelGenerators(stateOutput, itemOutput, modelOutput) {
    override fun run() {
    }
}