package com.markdownbookmod.network

import com.markdownbookmod.Markdownbookmod
import com.markdownbookmod.item.MarkdownBook
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.neoforged.neoforge.network.handling.IPayloadContext

data class UpdateMarkdownBookPayload(
    val hand: InteractionHand,
    val title: String,
    val content: String
) : CustomPacketPayload {

    companion object {
        val TYPE: CustomPacketPayload.Type<UpdateMarkdownBookPayload> = 
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Markdownbookmod.ID, "update_markdown_book"))
        
        val STREAM_CODEC: StreamCodec<ByteBuf, UpdateMarkdownBookPayload> = StreamCodec.composite(
            ByteBufCodecs.fromEnum(InteractionHand::class.java), UpdateMarkdownBookPayload::hand,
            ByteBufCodecs.STRING_UTF8, UpdateMarkdownBookPayload::title,
            ByteBufCodecs.STRING_UTF8, UpdateMarkdownBookPayload::content,
            ::UpdateMarkdownBookPayload
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun handle(context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player()
            val itemStack = player.getItemInHand(hand)
            
            if (itemStack.item is MarkdownBook) {
                val markdownBook = itemStack.item as MarkdownBook
                markdownBook.setTitleAndContents(itemStack, title, content)
                
                // Mark the inventory as dirty to ensure it's saved
                player.inventoryMenu.broadcastChanges()
            }
        }
    }
}