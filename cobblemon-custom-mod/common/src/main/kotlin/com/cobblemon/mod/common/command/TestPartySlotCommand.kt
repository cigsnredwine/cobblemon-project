/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object TestPartySlotCommand {

    private const val NAME = "testpartyslot"
    private const val PLAYER = "player"
    private const val SLOT = "slot"
    private const val PROPERTIES = "properties"
    private const val NO_SUCCESS = 0

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal(NAME)
                .permission(CobblemonPermissions.TEST_PARTY_SLOT)
                .then(CommandManager.argument(PLAYER, EntityArgumentType.player())
                    .then(CommandManager.argument(SLOT, IntegerArgumentType.integer(1, 6))
                        .then(CommandManager.argument(PROPERTIES, PokemonPropertiesArgumentType.properties())
                            .executes(this::execute))))
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.player(PLAYER)
        val slot = IntegerArgumentType.getInteger(context, SLOT)
        val properties = PokemonPropertiesArgumentType.getPokemonProperties(context, PROPERTIES)
        return if (player.party().get(slot - 1)?.createPokemonProperties(PokemonPropertyExtractor.ALL)
                ?.let { properties.isSubSetOf(it) } == true) Command.SINGLE_SUCCESS else NO_SUCCESS
    }
}