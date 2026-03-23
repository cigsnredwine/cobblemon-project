/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.moveselect

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.callback.MoveSelectDTO
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.net.messages.server.callback.move.MoveSelectCancelledPacket
import com.cobblemon.mod.common.net.messages.server.callback.move.MoveSelectedPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class MoveSelectConfiguration(
    val title: MutableText,
    val moves: List<MoveSelectDTO>,
    val onCancel: (MoveSelectGUI) -> Unit,
    val onBack: (MoveSelectGUI) -> Unit,
    val onSelect: (MoveSelectGUI, MoveSelectDTO) -> Unit,
)

class MoveSelectGUI(
    val config: MoveSelectConfiguration
) : Screen(Text.translatable("cobblemon.ui.interact.moveselect")) {
    companion object {
        const val WIDTH = 122
        const val HEIGHT = 133

        private val baseBackgroundResource = cobblemonResource("textures/gui/interact/move_select.png")
    }

    var closed = false

    constructor(
        title: MutableText,
        moves: List<MoveSelectDTO>,
        uuid: UUID
    ): this(
        MoveSelectConfiguration(
            title = title,
            moves = moves,
            onSelect = { gui, it ->
                CobblemonNetwork.sendToServer(MoveSelectedPacket(uuid = uuid, moves.indexOf(it)))
                gui.closeProperly()
            },
            onCancel = { CobblemonNetwork.sendToServer(MoveSelectCancelledPacket(uuid = uuid)) },
            onBack = MoveSelectGUI::close
        )
    )

    fun closeProperly() {
        closed = true
        close()
    }

    override fun init() {
        val x = (width - WIDTH) / 2
        val y = (height - HEIGHT) / 2

        config.moves.forEachIndexed { index, move ->
            addDrawableChild(
                MoveSlotButton(
                    x = x + 7,
                    y = y + 7 + ((MoveSlotButton.HEIGHT + 3) * index),
                    move = move.moveTemplate,
                    pp = move.pp,
                    ppMax = move.ppMax,
                    enabled = move.enabled
                ) { onPress(move) }
            )
        }

        // Add Exit Button
        addDrawableChild(
            ExitButton(
                pX = x + 92,
                pY = y + 115
            ) {
                playSound(CobblemonSounds.GUI_CLICK)
                config.onBack(this)
            }
        )

        super.init()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val x = (width - WIDTH) / 2
        val y = (height - HEIGHT) / 2

        blitk(
            matrixStack = context.matrices,
            texture = baseBackgroundResource,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT
        )

        // Render all added Widgets
        super.render(context, mouseX, mouseY, partialTicks)
    }

    private fun onPress(move: MoveSelectDTO) {
        if (!move.enabled) {
            return
        }
        playSound(CobblemonSounds.GUI_CLICK)
        config.onSelect(this, move)
    }

    override fun close() {
        if (!closed) {
            config.onCancel(this)
        }
        super.close()
    }

    override fun shouldCloseOnEsc() = true
    override fun shouldPause() = false

    fun playSound(soundEvent: SoundEvent) {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0F))
    }
}