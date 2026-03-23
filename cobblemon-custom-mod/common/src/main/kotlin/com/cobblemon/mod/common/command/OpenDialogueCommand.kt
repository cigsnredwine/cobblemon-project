/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.dialogue.Dialogues
import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.command.argument.DialogueArgumentType
import com.cobblemon.mod.common.util.alias
import com.cobblemon.mod.common.util.openDialogue
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object OpenDialogueCommand {
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(CommandManager.literal("opendialogue")
            .permission(CobblemonPermissions.OPEN_DIALOGUE)
            .then(
                CommandManager.argument("dialogue", DialogueArgumentType.dialogue())
                    .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                            .executes {
                                val dialogueId = DialogueArgumentType.getDialogue(it, "dialogue")
                                if (!Dialogues.dialogues.containsKey(dialogueId)) {
                                    it.source.sendMessage("Invalid dialogue: $dialogueId".text())
                                    return@executes Command.SINGLE_SUCCESS
                                }
                                val player = EntityArgumentType.getPlayer(it, "player")
                                return@executes execute(it.source, dialogueId, player)
                            }
                    )
            )
        )
        dispatcher.register(command.alias("opendialogue"))
    }

    private fun execute(source: ServerCommandSource, dialogueId: Identifier, player: ServerPlayerEntity): Int {
        val dialogue = Dialogues.dialogues[dialogueId] ?: return run {
            source.sendMessage("Invalid dialogue ID: $dialogueId".text())
            Command.SINGLE_SUCCESS
        }
        try {
            player.openDialogue(dialogue)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}