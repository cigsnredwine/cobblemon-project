/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.dialogue.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.PartyOverlay
import com.cobblemon.mod.common.client.gui.dialogue.DialogueScreen
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element

class DialogueFaceWidget(
    val dialogueScreen: DialogueScreen,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) : Drawable, Element {
    companion object {
        val frameResource = cobblemonResource("textures/gui/dialogue/dialogue_face.png")
        val frameBackground = cobblemonResource("textures/gui/dialogue/dialogue_face_background.png")
    }
    override fun setFocused(focused: Boolean) {}
    override fun isFocused() = false
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val face = dialogueScreen.dialogueDTO.currentPageDTO.speaker?.let { dialogueScreen.speakers[it] }?.face ?: return

        blitk(
            texture = frameBackground,
            matrixStack = context.matrices,
            x = x,
            y = y,
            width = width,
            height = height
        )

        context.enableScissor(
            x + 2,
            y + 2,
            x + 2 + width - 4,
            y + 2 + height - 4
        )
        context.matrices.push()
        context.matrices.translate(x.toDouble() + width / 2, y.toDouble(), 0.0)
        face.render(context, delta)
        context.disableScissor()
        context.matrices.pop()
        context.matrices.push()
        context.matrices.translate(0F, 0F, 100F)
        blitk(
            texture = frameResource,
            matrixStack = context.matrices,
            x = x,
            y = y,
            width = width,
            height = height
        )
        context.matrices.pop()
    }
}