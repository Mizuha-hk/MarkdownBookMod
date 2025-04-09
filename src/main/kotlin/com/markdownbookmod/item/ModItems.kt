package com.markdownbookmod.item

import com.markdownbookmod.Markdownbookmod
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModItems {
    val ITEMS = DeferredRegister.createItems(Markdownbookmod.ID);

    val MARKDOWNBOOK = register("markdown_book", { properties ->
        MarkdownBook(properties);
    }, { ->
        Item.Properties();
    });

    fun <T: Item> register(name: String, item: (Item.Properties) -> T, properties: Supplier<Item.Properties>): DeferredItem<T> {
        return ITEMS.register(name) { ->
            item(properties.get().setId(ResourceKey.create(Registries.ITEM, Markdownbookmod.prefix(name))));
        }
    }
}