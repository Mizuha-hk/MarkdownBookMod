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
            val customData = stack.get(DataComponents.CUSTOM_DATA)
            return customData?.copyTag()?.getString(TAG_TITLE) ?: ""
        }
        
        fun getContent(stack: ItemStack): String {
            val customData = stack.get(DataComponents.CUSTOM_DATA)
            return customData?.copyTag()?.getString(TAG_CONTENT) ?: ""
        }
        
        fun setTitle(stack: ItemStack, title: String) {
            val nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
            nbt.putString(TAG_TITLE, title)
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
        }
        
        fun setContent(stack: ItemStack, content: String) {
            val nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
            nbt.putString(TAG_CONTENT, content)
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
        }
        
        fun setTitleAndContent(stack: ItemStack, title: String, content: String) {
            val nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
            nbt.putString(TAG_TITLE, title)
            nbt.putString(TAG_CONTENT, content)
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
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
}