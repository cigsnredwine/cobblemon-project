/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.command.argument.PokemonStoreArgumentType
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object TestStoreCommand {

    private const val NAME = "teststore"
    private const val PLAYER = "player"
    private const val STORE = "store"
    private const val PROPERTIES = "properties"

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal(NAME)
            .permission(CobblemonPermissions.TEST_STORE)
            .then(argument(PLAYER, EntityArgumentType.player())
            .then(argument(STORE, PokemonStoreArgumentType.pokemonStore())
            .then(argument(PROPERTIES, PokemonPropertiesArgumentType.properties())
            .executes(this::execute))))
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.player(PLAYER)
        val storeType = PokemonStoreArgumentType.pokemonStoreFrom(context, STORE)
        val properties = PokemonPropertiesArgumentType.getPokemonProperties(context, PROPERTIES)
        return storeType.storeFetcher(player)
            .count(properties::matches)
    }

}