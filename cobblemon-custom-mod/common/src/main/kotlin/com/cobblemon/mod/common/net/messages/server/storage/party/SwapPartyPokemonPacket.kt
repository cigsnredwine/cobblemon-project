/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.storage.party

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.storage.party.PartyPosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cobblemon.mod.common.net.serverhandling.storage.party.SwapPartyPokemonHandler
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Tells the server to swap two Pokémon in the player's party.
 *
 * Handled by [SwapPartyPokemonHandler].
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class SwapPartyPokemonPacket(val pokemon1ID: UUID, val position1: PartyPosition, val pokemon2ID: UUID, val position2: PartyPosition) : NetworkPacket<SwapPartyPokemonPacket> {
    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pokemon1ID)
        buffer.writePartyPosition(position1)
        buffer.writeUuid(pokemon2ID)
        buffer.writePartyPosition(position2)
    }
    companion object {
        val ID = cobblemonResource("swap_party_pokemon")
        fun decode(buffer: PacketByteBuf) = SwapPartyPokemonPacket(buffer.readUuid(), buffer.readPartyPosition(), buffer.readUuid(), buffer.readPartyPosition())
    }
}