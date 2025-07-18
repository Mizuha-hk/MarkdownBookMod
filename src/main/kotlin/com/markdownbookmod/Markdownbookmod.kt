package com.markdownbookmod

import com.markdownbookmod.block.ModBlocks
import com.markdownbookmod.item.ModItems
import com.markdownbookmod.network.ModNetworking
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import java.util.*

/**
 * Main mod class.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(Markdownbookmod.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object Markdownbookmod {
    const val ID = "markdownbookmod"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.log(Level.INFO, "MarkdownBookMod is loading...")

        ModBlocks.BLOCKS.register(MOD_BUS)
        ModItems.ITEMS.register(MOD_BUS)
        
        // Register network handlers
        MOD_BUS.addListener(::registerPayloads)

        val obj = runForDist(clientTarget = {
            MOD_BUS.addListener(::onClientSetup)
            Minecraft.getInstance()
        }, serverTarget = {
            MOD_BUS.addListener(::onServerSetup)
            "test"
        })

        println(obj)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.log(Level.INFO, "Hello! This is working!")
    }
    
    private fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        ModNetworking.register(event)
    }

    fun prefix(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ID, name.lowercase(Locale.ROOT))
    }
}
