package com.markdownbookmod.screen

import com.markdownbookmod.Markdownbookmod
import com.markdownbookmod.core.MarkdownProcessor
import com.markdownbookmod.item.MarkdownBook
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class MarkdownViewScreen(private val itemStack: ItemStack, private val hand: InteractionHand): Screen(Component.translatable("string.markdown_book.view_title")) {
    private lateinit var editButton: Button
    private lateinit var closeButton: Button
    private val markdownBook = itemStack.item as MarkdownBook
    private val markdownProcessor = MarkdownProcessor()
    private var renderedLines: List<String> = emptyList()

    val LOGGER: Logger = LogManager.getLogger(Markdownbookmod.ID)

    override fun init() {
        super.init()

        val centerX = width / 2
        val startY = height - 40

        // Process markdown content
        val markdownText = markdownBook.getMarkdownText(itemStack)
        val renderedText = markdownProcessor.parseToMinecraftText(markdownText)
        
        // Split into lines for rendering
        renderedLines = renderedText.split('\n')

        editButton = Button.builder(
            Component.literal("Edit")
        ) { _ ->
            openEditScreen()
        }.bounds(
            centerX - 75,
            startY,
            70,
            20
        ).build()
        addRenderableWidget(editButton)

        closeButton = Button.builder(
            Component.literal("Close")
        ) { _ ->
            onClose()
        }.bounds(
            centerX + 5,
            startY,
            70,
            20)
            .build()
        addRenderableWidget(closeButton)
    }

    private fun openEditScreen() {
        val minecraft = minecraft!!
        minecraft.setScreen(MarkdownBookScreen(itemStack, hand))
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float){
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        val title = markdownBook.getTitle(itemStack)
        val displayTitle = if (title.isNotEmpty()) title else "Untitled"
        
        guiGraphics.drawCenteredString(
            font,
            displayTitle,
            width / 2,
            20,
            0xFFFFFF
        )

        // Render markdown content
        var yOffset = 40
        val leftMargin = 20
        val rightMargin = width - 20
        val maxWidth = rightMargin - leftMargin
        
        for (line in renderedLines) {
            if (yOffset > height - 80) break // Stop if we're getting close to the buttons
            
            // Word wrap long lines
            val wrappedLines = wrapText(line, maxWidth)
            for (wrappedLine in wrappedLines) {
                guiGraphics.drawString(
                    font,
                    wrappedLine,
                    leftMargin,
                    yOffset,
                    0xFFFFFF
                )
                yOffset += font.lineHeight + 2
            }
            yOffset += 2 // Extra space between paragraphs
        }
    }

    private fun wrapText(text: String, maxWidth: Int): List<String> {
        if (font.width(text) <= maxWidth) {
            return listOf(text)
        }
        
        val words = text.split(' ')
        val lines = mutableListOf<String>()
        var currentLine = ""
        
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (font.width(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                    currentLine = word
                } else {
                    // Single word is too long, just add it
                    lines.add(word)
                }
            }
        }
        
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        
        return lines
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}