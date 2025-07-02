package com.markdownbookmod.screen

import com.markdownbookmod.Markdownbookmod
import com.markdownbookmod.item.MarkdownBook
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.MultiLineEditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class MarkdownBookScreen(private val itemStack: ItemStack): Screen(Component.translatable("string.markdown_book.title")) {
    private lateinit var titleEditBox: EditBox
    private lateinit var contentEditBox: MultiLineEditBox
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private val markdownBook = itemStack.item as MarkdownBook

    val LOGGER: Logger = LogManager.getLogger(Markdownbookmod.ID)

    override fun init() {
        super.init()

        val centerX = width / 2
        val startY = 40

        titleEditBox = EditBox(
            font,
            centerX - 150,
            startY,
            300,
            20,
            Component.literal("Title")
        )
        titleEditBox.value = markdownBook.getTitle(itemStack)
        titleEditBox.setMaxLength(100)
        addRenderableWidget(titleEditBox)

        LOGGER.info("Initialized Title with value: ${titleEditBox.value}")

        // Content EditBox
        contentEditBox = MultiLineEditBox(
            font,
            centerX - 150,
            startY + 40,
            300,
            120,
            Component.literal("Content"),
            Component.literal("Enter your markdown text here")
        )
        contentEditBox.value = markdownBook.getMarkdownText(itemStack)
        contentEditBox.setCharacterLimit(10000)
        addRenderableWidget(contentEditBox)
        LOGGER.info("Initialized Content with value: ${contentEditBox.value}")

        saveButton = Button.builder(
            Component.literal("Save")
        ) { _ ->
            saveAndClose()
        }.bounds(
            centerX -75,
            startY + 180,
            70,
            20
        ).build()
        addRenderableWidget(saveButton)

        cancelButton = Button.builder(
            Component.literal("Cancel")
        ) { _ ->
            onClose()
        }.bounds(
            centerX + 5,
            startY + 180,
            70,
            20)
            .build()
        addRenderableWidget(cancelButton)
    }

    private fun saveAndClose() {
        LOGGER.info("Saving Markdown Book with Title: ${titleEditBox.value} and Content: ${contentEditBox.value}")
        markdownBook.setTitleAndContents(itemStack, titleEditBox.value, contentEditBox.value)
        onClose()
    }

    override  fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float){
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        guiGraphics.drawCenteredString(
            font,
            "Markdown Book Editor",
            width / 2,
            20,
            0xFFFFFF
        )

        guiGraphics.drawCenteredString(
            font,
            "Title:",
            titleEditBox.x,
            titleEditBox.y - 12,
            0xFFFFFF
        )

        guiGraphics.drawString(
            font,
            "Content:",
            contentEditBox.x,
            contentEditBox.y - 12,
            0xFFFFFF
        )
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}