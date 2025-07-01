package com.markdownbookmod.data.models

import com.markdownbookmod.Markdownbookmod
import com.markdownbookmod.block.ModBlocks
import com.markdownbookmod.item.ModItems
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.core.Holder
import net.minecraft.data.CachedOutput
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

class ModelGenerator(packOutput: PackOutput) : ModelProvider(packOutput, Markdownbookmod.ID) {
    private val blocks: PackOutput.PathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates")
    private val items: PackOutput.PathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items")
    private val models: PackOutput.PathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models")

    override fun run(output: CachedOutput): CompletableFuture<*> {
        val itemModelOutput = ItemInfoCollector(this::getKnownItems)
        val blockModelOutput = BlockStateGeneratorCollector(this::getKnownBlocks)
        val modelOutput = SimpleModelCollector()
        this.registerModels(BlockModelGenerator(blockModelOutput, itemModelOutput, modelOutput), ItemModelGenerator(itemModelOutput, modelOutput))

        return CompletableFuture.allOf(blockModelOutput.save(output, this.blocks), modelOutput.save(output, this.models), itemModelOutput.save(output, this.items))
    }

    override fun getKnownItems(): Stream<out Holder<Item>> {
        return ModItems.ITEMS.getEntries().stream()
    }

    override fun getKnownBlocks(): Stream<out Holder<Block>> {
        return ModBlocks.BLOCKS.getEntries().stream()
    }
}