/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.storage.pc

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cobblemon.mod.common.net.messages.PokemonDTO
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Sets a specific Pokémon in a specific slot of the client-side representation of a PC.
 *
 * Handled by [com.cobblemon.mod.common.client.net.storage.pc.SetPCPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class SetPCPokemonPacket internal constructor(val storeID: UUID, val storePosition: PCPosition, val pokemonDTO: PokemonDTO) : NetworkPacket<SetPCPokemonPacket> {

    override val id = ID

    constructor(storeID: UUID, storePosition: PCPosition, pokemon: Pokemon) : this(storeID, storePosition, PokemonDTO(pokemon, true))

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(this.storeID)
        buffer.writePCPosition(this.storePosition)
        this.pokemonDTO.encode(buffer)
    }

    companion object {
        val ID = cobblemonResource("set_pc_pokemon")
        fun decode(buffer: PacketByteBuf) = SetPCPokemonPacket(buffer.readUuid(), buffer.readPCPosition(), PokemonDTO().apply { decode(buffer) })
    }

}