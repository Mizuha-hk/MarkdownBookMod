package com.markdownbookmod.data

import com.markdownbookmod.Markdownbookmod
import com.markdownbookmod.data.lang.EnusLangGenerator
import com.markdownbookmod.data.lang.JajpLangGenerator
import com.markdownbookmod.data.models.ModelGenerator
import net.minecraft.DetectedVersion
import net.minecraft.data.metadata.PackMetadataGenerator
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.metadata.pack.PackMetadataSection
import net.minecraft.util.InclusiveRange
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.apache.logging.log4j.LogManager
import java.util.*

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Markdownbookmod.ID)
class DataGenerator {
    val LOGGER = LogManager.getLogger(Markdownbookmod.ID)

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent.Client){
        LOGGER.info("Generating data...")
        val generator = event.generator
        val output = generator.packOutput

        generator.addProvider(true, ModelGenerator(output))

        //language
        generator.addProvider(true, EnusLangGenerator(output))
        generator.addProvider(true, JajpLangGenerator(output))

        generator.addProvider(true, PackMetadataGenerator(output).add(PackMetadataSection.TYPE, PackMetadataSection(
            Component.literal("MarkdownBookMod Resource"),
            DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
            Optional.of(InclusiveRange(0, Integer.MAX_VALUE)))))
    }
}