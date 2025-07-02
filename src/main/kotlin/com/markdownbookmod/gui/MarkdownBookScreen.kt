package com.markdownbookmod.gui

import com.markdownbookmod.item.MarkdownBook
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.MultiLineEditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class MarkdownBookScreen(private val itemStack: ItemStack) : Screen(Component.translatable("gui.markdownbookmod.markdown_book")) {
    
    private lateinit var titleEditBox: EditBox
    private lateinit var contentEditBox: MultiLineEditBox
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    
    private var originalTitle: String = ""
    private var originalContent: String = ""
    
    override fun init() {
        super.init()
        
        // Store original values
        originalTitle = MarkdownBook.getTitle(itemStack)
        originalContent = MarkdownBook.getContent(itemStack)
        
        // Title input box
        titleEditBox = EditBox(
            font,
            width / 2 - 100,
            40,
            200,
            20,
            Component.translatable("gui.markdownbookmod.title")
        )
        titleEditBox.value = originalTitle
        titleEditBox.setMaxLength(100)
        addRenderableWidget(titleEditBox)
        
        // Content input box
        contentEditBox = MultiLineEditBox(
            font,
            width / 2 - 150,
            80,
            300,
            height - 150,
            Component.translatable("gui.markdownbookmod.content"),
            Component.translatable("gui.markdownbookmod.content")
        )
        contentEditBox.setValue(originalContent)
        contentEditBox.setMaxLength(32767)
        addRenderableWidget(contentEditBox)
        
        // Save button
        saveButton = Button.builder(
            Component.translatable("gui.markdownbookmod.save")
        ) { _ ->
            saveAndClose()
        }.bounds(width / 2 - 100, height - 40, 80, 20).build()
        addRenderableWidget(saveButton)
        
        // Cancel button
        cancelButton = Button.builder(
            Component.translatable("gui.markdownbookmod.cancel")
        ) { _ ->
            onClose()
        }.bounds(width / 2 + 20, height - 40, 80, 20).build()
        addRenderableWidget(cancelButton)
        
        setInitialFocus(titleEditBox)
    }
    
    private fun saveAndClose() {
        val title = titleEditBox.value
        val content = contentEditBox.value
        
        // Update the ItemStack with new title and content
        MarkdownBook.setTitleAndContent(itemStack, title, content)
        
        onClose()
    }
    
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        
        // Draw title label
        guiGraphics.drawCenteredString(
            font,
            Component.translatable("gui.markdownbookmod.title"),
            width / 2,
            25,
            0xFFFFFF
        )
        
        // Draw content label
        guiGraphics.drawString(
            font,
            Component.translatable("gui.markdownbookmod.content"),
            width / 2 - 150,
            65,
            0xFFFFFF
        )
        
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }
    
    override fun isPauseScreen(): Boolean {
        return false
    }
    
    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        // Allow Tab to switch between title and content
        if (keyCode == 258) { // Tab key
            if (titleEditBox.isFocused) {
                titleEditBox.setFocused(false)
                contentEditBox.setFocused(true)
                return true
            } else if (contentEditBox.isFocused) {
                contentEditBox.setFocused(false)
                titleEditBox.setFocused(true)
                return true
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
    
    override fun onClose() {
        minecraft?.setScreen(null)
    }
}