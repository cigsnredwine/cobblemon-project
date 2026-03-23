/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.storage.party

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.storage.party.PartyPosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cobblemon.mod.common.net.messages.PokemonDTO
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Adds the given Pokémon to a specific location in the client storage. This should be a new
 * Pokémon that the client doesn't know about yet.
 *
 * Handled by [com.cobblemon.mod.common.client.net.storage.party.SetPartyPokemonHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
*/
class SetPartyPokemonPacket internal constructor(val storeID: UUID, val storePosition: PartyPosition, val pokemonDTO: PokemonDTO) : NetworkPacket<SetPartyPokemonPacket> {

    override val id = ID

    constructor(storeID: UUID, storePosition: PartyPosition, pokemon: Pokemon) : this(storeID, storePosition, PokemonDTO(pokemon, true))

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(this.storeID)
        buffer.writePartyPosition(this.storePosition)
        this.pokemonDTO.encode(buffer)
    }

    companion object {
        val ID = cobblemonResource("set_party_pokemon")
        fun decode(buffer: PacketByteBuf) = SetPartyPokemonPacket(buffer.readUuid(), buffer.readPartyPosition(), PokemonDTO().apply { decode(buffer) })
    }

}