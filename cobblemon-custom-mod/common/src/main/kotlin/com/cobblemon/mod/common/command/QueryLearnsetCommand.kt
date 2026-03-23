/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.pokemon.moves.LearnsetQuery
import com.cobblemon.mod.common.command.argument.MoveArgumentType
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object QueryLearnsetCommand {

    private const val NAME = "querylearnset"
    private const val PLAYER = "player"
    private const val SLOT = "slot"
    private const val MOVE = "move"
    private const val NO_SUCCESS = 0

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal(NAME)
                .permission(CobblemonPermissions.QUERY_LEARNSET)
                .then(argument(PLAYER, EntityArgumentType.player())
                .then(argument(SLOT, PartySlotArgumentType.partySlot())
                .then(argument(MOVE, MoveArgumentType.move())
                .executes(this::execute))))
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.player(PLAYER)
        val pokemon = PartySlotArgumentType.getPokemonOf(context, SLOT, player)
        val move = MoveArgumentType.getMove(context, MOVE)
        return if (LearnsetQuery.ANY.canLearn(move, pokemon.form.moves)) Command.SINGLE_SUCCESS else NO_SUCCESS
    }

}