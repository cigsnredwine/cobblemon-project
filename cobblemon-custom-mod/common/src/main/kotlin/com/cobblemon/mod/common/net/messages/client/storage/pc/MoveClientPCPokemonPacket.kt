/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.storage.pc

import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cobblemon.mod.common.net.messages.client.storage.MoveClientPokemonPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Moves a Pokémon from one part of a PC to another on the client side.
 *
 * Handled by [com.cobblemon.mod.common.client.net.storage.pc.MoveClientPCPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class MoveClientPCPokemonPacket(storeID: UUID, pokemonID: UUID, newPosition: PCPosition) : MoveClientPokemonPacket<PCPosition, MoveClientPCPokemonPacket>(storeID, pokemonID, newPosition) {
    override val id = ID
    override fun encodePosition(buffer: PacketByteBuf, position: PCPosition) = buffer.writePCPosition(position)
    companion object {
        val ID = cobblemonResource("move_client_pc_pokemon")
        fun decode(buffer: PacketByteBuf) = MoveClientPCPokemonPacket(buffer.readUuid(), buffer.readUuid(), buffer.readPCPosition())
    }
}