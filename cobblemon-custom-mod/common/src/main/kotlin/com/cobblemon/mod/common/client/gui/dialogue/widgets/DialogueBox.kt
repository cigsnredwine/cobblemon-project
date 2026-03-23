/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.dialogue.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.dialogue.DialogueScreen
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.net.messages.client.dialogue.dto.DialogueInputDTO
import com.cobblemon.mod.common.net.messages.server.dialogue.InputToDialoguePacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.util.Language

/**
 * UI element for showing the lines of dialogue text.
 *
 * @author Hiroku
 * @since December 29th, 2023
 */
class DialogueBox(
    val dialogueScreen: DialogueScreen,
    val x: Int = 0,
    val y: Int = 0,
    val frameWidth: Int,
    height: Int,
    messages: List<MutableText>
): AlwaysSelectedEntryListWidget<DialogueBox.DialogueLine>(
    MinecraftClient.getInstance(),
    frameWidth - 14,
    height, // height
    1, // top
    1 + height, // bottom
    LINE_HEIGHT
) {
    val dialogue = dialogueScreen.dialogueDTO
    var opacity = 1F
    private var scrolling = false

    val appropriateX: Int
        get() = x
    val appropriateY: Int
        get() = y

    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)

        val textRenderer = MinecraftClient.getInstance().textRenderer

        messages
            .flatMap { Language.getInstance().reorder(textRenderer.textHandler.wrapLines(it, LINE_WIDTH, it.style)) }
            .forEach { addEntry(DialogueLine(it)) }
    }

    private fun correctSize() {
        val textBoxHeight = height
        updateSize(width, textBoxHeight, appropriateY + 6, appropriateY + 6 + textBoxHeight)
        setLeftPos(appropriateX + 8)
    }

    companion object {
        const val LINE_HEIGHT = 10
        const val LINE_WIDTH = 142
        private val boxResource = cobblemonResource("textures/gui/dialogue/dialogue_box.png")
    }

    override fun addEntry(entry: DialogueLine): Int {
        return super.addEntry(entry)
    }

    override fun getRowWidth(): Int {
        return 80
    }

    override fun getScrollbarPositionX(): Int {
        return left + 144
    }

    override fun getScrollAmount(): Double {
        return super.getScrollAmount()
    }

    private fun scaleIt(i: Number): Int {
        return (client.window.scaleFactor * i.toFloat()).toInt()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()
        blitk(
            matrixStack = context.matrices,
            texture = boxResource,
            x = left - 8,
            y = appropriateY - 1,
            height = height + 12, // used to be FRAME_HEIGHT as below
            width = frameWidth,
            alpha = opacity
        )


        super.render(context, mouseX, mouseY, partialTicks)
//        context.disableScissor()
    }

    override fun enableScissor(context: DrawContext) {
        val textBoxHeight = height
        context.enableScissor(
            left,
            appropriateY + 7,
            left + width - 10,
            appropriateY + 7 + textBoxHeight
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val toggleOffsetY = 92

        // TODO change this coordinate check to just be "anywhere the scroll bar isn't"
        if (!dialogueScreen.waitingForServerUpdate &&
            mouseX > (left) &&
            mouseX < (left + 160) &&
            mouseY > (appropriateY) &&
            mouseY < (appropriateY + height)
        ) {
            if (dialogue.dialogueInput.allowSkip && dialogue.dialogueInput.inputType in listOf(DialogueInputDTO.InputType.NONE, DialogueInputDTO.InputType.AUTO_CONTINUE)) {
                dialogueScreen.sendToServer(InputToDialoguePacket(dialogue.dialogueInput.inputId, "skip!"))
                return true
            }
        }

        updateScrollingState(mouseX, mouseY)
        if (scrolling) {
            focused = getEntryAtPosition(mouseX, mouseY)
            isDragging = true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scrolling) {
            if (mouseY < top) {
                scrollAmount = 0.0
            } else if (mouseY > bottom) {
                scrollAmount = maxScroll.toDouble()
            } else {
                scrollAmount += deltaY
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollingState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= this.scrollbarPositionX.toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= top
                && mouseY < bottom
    }

    class DialogueLine(val line: OrderedText) : Entry<DialogueLine>() {
        override fun getNarration() = "".text()
        override fun render(
            context: DrawContext,
            index: Int,
            rowTop: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            partialTicks: Float
        ) {
            drawScaledText(
                context,
                line,
                rowLeft - 38,
                rowTop - 2
            )
        }
    }
}