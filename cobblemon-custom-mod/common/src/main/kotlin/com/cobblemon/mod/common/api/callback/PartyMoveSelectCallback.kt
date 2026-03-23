/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.callback

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.moves.Move
import com.cobblemon.mod.common.net.messages.client.callback.OpenPartyMoveCallbackPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.lang
import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText

/**
 * Used for opening a party select screen for players which routes to move selection when they choose a Pokémon.
 * You can do the same sort of thing with combined [MoveSelectCallbacks] and [PartySelectCallbacks] but it isn't
 * as smooth an experience.
 *
 * @author Hiroku
 * @since July 29th, 2023
 */
object PartyMoveSelectCallbacks {
    val callbacks = mutableMapOf<UUID, PartyMoveSelectCallback>()

    @JvmOverloads
    fun create(
        player: ServerPlayerEntity,
        partyTitle: MutableText = lang("ui.party"),
        pokemon: List<Pair<PartySelectPokemonDTO, List<MoveSelectDTO>>>,
        cancel: (ServerPlayerEntity) -> Unit = {},
        handler: (ServerPlayerEntity, pokemonIndex: Int, PartySelectPokemonDTO, moveIndex: Int, MoveSelectDTO) -> Unit
    ) {
        val callback = PartyMoveSelectCallback(
            pokemon = pokemon,
            cancel = cancel,
            handler = handler
        )
        callbacks[player.uuid] = callback
        player.sendPacket(OpenPartyMoveCallbackPacket(callback.uuid, partyTitle, callback.pokemon))
    }

    @JvmOverloads
    fun createFromPokemon(
        player: ServerPlayerEntity,
        partyTitle: MutableText = lang("ui.party"),
        pokemon: List<Pokemon>,
        moves: (Pokemon) -> List<Move> = { it.moveSet.getMoves() },
        canSelectPokemon: (Pokemon) -> Boolean = { true },
        canSelectMove: (Pokemon, Move) -> Boolean = { _, _ -> true },
        cancel: (ServerPlayerEntity) -> Unit = {},
        handler: (Pokemon, Move) -> Unit
    ) {
        val pokemonList = mutableListOf<Pair<PartySelectPokemonDTO, List<MoveSelectDTO>>>()
        for (pk in pokemon) {
            val enabled = canSelectPokemon(pk)
            val moveList = moves(pk).map { MoveSelectDTO(it, enabled = canSelectMove(pk, it)) }
            pokemonList.add(PartySelectPokemonDTO(pk, enabled) to moveList)
        }

        create(
            player = player,
            partyTitle = partyTitle,
            pokemon = pokemonList,
            cancel = cancel,
            handler = { _, pkIndex, _, moveIndex, _ ->
                handler(pokemon[pkIndex], pokemon[pkIndex].moveSet[moveIndex] ?: return@create)
            }
        )
    }

    fun handleCancelled(player: ServerPlayerEntity, uuid: UUID) {
        val callback = callbacks[player.uuid] ?: return
        if (callback.uuid != uuid) {
            return
        }
        callbacks.remove(player.uuid)
        callback.cancel(player)
    }

    fun handleCallback(player: ServerPlayerEntity, uuid: UUID, pokemonIndex: Int, moveIndex: Int) {
        val callback = callbacks[player.uuid] ?: return
        callbacks.remove(player.uuid)
        if (callback.uuid != uuid) {
            return Cobblemon.LOGGER.warn("A party move select callback ran but with a mismatching UUID from ${player.gameProfile.name}. Hacking attempts?")
        } else if (pokemonIndex >= callback.pokemon.size) {
            return Cobblemon.LOGGER.warn("${player.gameProfile.name} used party move select callback with a Pokémon index that was out of bounds. Hacking attempts? Tried $pokemonIndex, possible size was ${callback.pokemon.size}")
        }

        val (pokemon, moves) = callback.pokemon[pokemonIndex]

        if (!pokemon.enabled) {
            return Cobblemon.LOGGER.warn("${player.gameProfile.name} used party move select callback with a Pokémon that is not enabled. Hacking attempts?")
        } else if (moveIndex >= moves.size) {
            return Cobblemon.LOGGER.warn("${player.gameProfile.name} used party move select callback with a move index that was out of bounds. Hacking attempts? Tried $pokemonIndex-$moveIndex, possible size was ${moves.size}")
        }

        val move = moves[moveIndex]

        if (!move.enabled) {
            Cobblemon.LOGGER.warn("${player.gameProfile.name} used party move select callback with a move that is not enabled. Hacking attempts?")
        } else {
            callback.handler(player, pokemonIndex, pokemon, moveIndex, move)
        }
    }
}

class PartyMoveSelectCallback(
    val uuid: UUID = UUID.randomUUID(),
    val pokemon: List<Pair<PartySelectPokemonDTO, List<MoveSelectDTO>>>,
    val cancel: (ServerPlayerEntity) -> Unit = {},
    val handler: (ServerPlayerEntity, pokemonIndex: Int, PartySelectPokemonDTO, moveIndex: Int, MoveSelectDTO) -> Unit
)