package com.markdownbookmod.item

import com.markdownbookmod.screen.MarkdownBookScreen
import com.markdownbookmod.screen.MarkdownViewScreen
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

class MarkdownBook(properties: Properties) : Item(properties) {

    companion object{
        const val TITLE_TAG = "Title"
        const val MARKDOWN_TEXT_TAG = "MarkdownText"
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val itemStack: ItemStack = player.getItemInHand(hand)
        if (level.isClientSide) {
            if (player.isShiftKeyDown) {
                // Shift + right click = edit mode
                openMarkdownBookScreen(itemStack, hand)
            } else {
                // Right click = view mode
                openMarkdownViewScreen(itemStack, hand)
            }
        }

        return InteractionResult.SUCCESS
    }

    @OnlyIn(Dist.CLIENT)
    private fun openMarkdownBookScreen(itemStack: ItemStack, hand: InteractionHand){
        val minecraft = Minecraft.getInstance()
        minecraft.setScreen(MarkdownBookScreen(itemStack, hand))
    }

    @OnlyIn(Dist.CLIENT)
    private fun openMarkdownViewScreen(itemStack: ItemStack, hand: InteractionHand){
        val minecraft = Minecraft.getInstance()
        minecraft.setScreen(MarkdownViewScreen(itemStack, hand))
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val title = getTitle(stack)
        if(title.isNotEmpty()){
            tooltipComponents.add(Component.literal("Title: $title"))
        }

        val text = getMarkdownText(stack)
        if(text.isNotEmpty()){
            tooltipComponents.add(Component.literal("Has content"))
        }
        
        // Add usage instructions
        tooltipComponents.add(Component.literal("Right-click: View"))
        tooltipComponents.add(Component.literal("Shift+Right-click: Edit"))
    }

    fun getMarkdownText(stack: ItemStack): String {
        val customData = stack.get(DataComponents.CUSTOM_DATA)
        return customData?.copyTag()?.getString(MARKDOWN_TEXT_TAG) ?: ""
    }

    fun getTitle(stack: ItemStack): String {
        val customData = stack.get(DataComponents.CUSTOM_DATA)
        return customData?.copyTag()?.getString(TITLE_TAG) ?: ""
    }

    fun setTitleAndContents(stack: ItemStack, title: String, text: String){
        val tag = CompoundTag()
        tag.putString(TITLE_TAG, title)
        tag.putString(MARKDOWN_TEXT_TAG, text)
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
    }
}