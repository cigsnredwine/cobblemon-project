/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.dialogue.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawCenteredText
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.dialogue.DialogueScreen
import com.cobblemon.mod.common.net.messages.client.dialogue.dto.DialogueInputDTO
import com.cobblemon.mod.common.net.messages.server.dialogue.InputToDialoguePacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class DialogueTextInputWidget(
    val dialogueScreen: DialogueScreen,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    maxLength: Int = 100
) : TextFieldWidget(
    MinecraftClient.getInstance().textRenderer,
    x,
    y,
    width,
    height,
    "gui_dialogue_text_input".text()
) {
    init {
        setMaxLength(maxLength)
        isFocused = true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (dialogueScreen.dialogueDTO.dialogueInput.inputType != DialogueInputDTO.InputType.TEXT || dialogueScreen.waitingForServerUpdate) {
            return false
        }

        return if (mouseX.toInt() in x..(x + width) && mouseY.toInt() in y..(y + height)) {
            isFocused = true
            true
        } else {
            false
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (dialogueScreen.dialogueDTO.dialogueInput.inputType != DialogueInputDTO.InputType.TEXT || dialogueScreen.waitingForServerUpdate) {
            return
        }
        if (cursor != text.length) {
            setCursorToEnd()
        }

        blitk(
            matrixStack = context.matrices,
            x = x,
            y = y,
            width = width,
            height = height,
            texture = cobblemonResource("textures/gui/dialogue/dialogue_text_input.png")
        )

        drawCenteredText(
            context = context,
            text = (if (isFocused) "$text|" else text).text(),
            x = x + width / 2F,
            y = y + height / 2 - 4,
            shadow = true,
            colour = 0xFFFFFF
        )
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == InputUtil.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.dialogueScreen.sendToServer(InputToDialoguePacket(dialogueScreen.dialogueDTO.dialogueInput.inputId, text.trim()))
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}