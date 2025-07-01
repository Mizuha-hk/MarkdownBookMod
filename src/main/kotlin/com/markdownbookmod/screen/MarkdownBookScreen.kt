package com.markdownbookmod.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class MarkdownBookScreen(level: Level, player: Player): Screen(Component.translatable("string.markdown_book.title")) {
    val level: Level = level
    private val player = player
}