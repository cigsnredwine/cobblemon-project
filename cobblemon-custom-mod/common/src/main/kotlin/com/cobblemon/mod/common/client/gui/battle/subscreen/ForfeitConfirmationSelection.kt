/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.battles.ForfeitActionResponse
import com.cobblemon.mod.common.client.battle.SingleActionRequest
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.gui.battle.BattleOverlay
import com.cobblemon.mod.common.client.gui.battle.widgets.BattleOptionTile
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack

class ForfeitConfirmationSelection(
    battleGUI: BattleGUI,
    request: SingleActionRequest
) : BattleActionSelection(
    battleGUI,
    request,
    x = 12,
    y = 12,
    width = 250,
    height = 100,
    battleLang("ui.forfeit_confirmation")
) {

    val forfeitButton: BattleOptionTile
    val backButton = BattleBackButton(x - 3F, MinecraftClient.getInstance().window.scaledHeight - 22F )

    init {
        val x = (MinecraftClient.getInstance().window.scaledWidth / 2) - (BattleOptionTile.OPTION_WIDTH / 2)
        val y = (MinecraftClient.getInstance().window.scaledHeight / 2) - (BattleOptionTile.OPTION_HEIGHT / 2)

        forfeitButton = BattleOptionTile(battleGUI, x, y, BattleGUI.runResource, battleLang("ui.forfeit")) {
            battleGUI.selectAction(request, ForfeitActionResponse())
            playDownSound(MinecraftClient.getInstance().soundManager)
        }
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (opacity <= 0.05F) {
            return
        }
        forfeitButton.render(context, mouseX, mouseY, delta)
        backButton.render(context.matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (backButton.isHovered(mouseX, mouseY)) {
            battleGUI.changeActionSelection(null)
            playDownSound(MinecraftClient.getInstance().soundManager)
            return true
        }

        return forfeitButton.mouseClicked(mouseX, mouseY, button)
    }

}