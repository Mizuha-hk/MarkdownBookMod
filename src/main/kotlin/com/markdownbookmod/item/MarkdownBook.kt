package com.markdownbookmod.item

import com.markdownbookmod.gui.MarkdownBookScreen
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

class MarkdownBook(properties: Properties) : Item(properties) {
    
    companion object {
        const val TAG_TITLE = "title"
        const val TAG_CONTENT = "content"
        
        fun getTitle(stack: ItemStack): String {
            return try {
                val customData = stack.get(DataComponents.CUSTOM_DATA)
                customData?.copyTag()?.getString(TAG_TITLE) ?: ""
            } catch (e: Exception) {
                ""
            }
        }
        
        fun getContent(stack: ItemStack): String {
            return try {
                val customData = stack.get(DataComponents.CUSTOM_DATA)
                customData?.copyTag()?.getString(TAG_CONTENT) ?: ""
            } catch (e: Exception) {
                ""
            }
        }
        
        fun setTitle(stack: ItemStack, title: String) {
            try {
                val nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
                nbt.putString(TAG_TITLE, title.take(100)) // Limit title length
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
            } catch (e: Exception) {
                // Silently fail if NBT operation fails
            }
        }
        
        fun setContent(stack: ItemStack, content: String) {
            try {
                val nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
                nbt.putString(TAG_CONTENT, content.take(32767)) // Limit content length
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
            } catch (e: Exception) {
                // Silently fail if NBT operation fails
            }
        }
        
        fun setTitleAndContent(stack: ItemStack, title: String, content: String) {
            try {
                val nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
                nbt.putString(TAG_TITLE, title.take(100)) // Limit title length
                nbt.putString(TAG_CONTENT, content.take(32767)) // Limit content length
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
            } catch (e: Exception) {
                // Silently fail if NBT operation fails
            }
        }
    }
    
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        
        if (level.isClientSide) {
            openGui(itemStack)
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide)
    }
    
    @OnlyIn(Dist.CLIENT)
    private fun openGui(itemStack: ItemStack) {
        Minecraft.getInstance().setScreen(MarkdownBookScreen(itemStack))
    }
    
    override fun getName(stack: ItemStack): Component {
        val title = getTitle(stack)
        return if (title.isNotEmpty()) {
            Component.literal(title)
        } else {
            super.getName(stack)
        }
    }
    
    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: net.minecraft.world.item.TooltipFlag) {
        val title = getTitle(stack)
        val content = getContent(stack)
        
        if (title.isNotEmpty()) {
            tooltipComponents.add(Component.literal("Title: $title").withStyle { it.withColor(0x55FF55) })
        }
        
        if (content.isNotEmpty()) {
            val preview = if (content.length > 50) {
                content.take(50) + "..."
            } else {
                content
            }
            tooltipComponents.add(Component.literal(preview).withStyle { it.withColor(0xAAAAAA) })
        }
        
        if (title.isEmpty() && content.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.markdownbookmod.markdown_book.tooltip").withStyle { it.withColor(0x888888) })
        }
        
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}