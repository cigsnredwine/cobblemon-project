/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.status.PersistentStatus
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class StatusUpdatePacket(pokemon: () -> Pokemon, value: PersistentStatus?): SingleUpdatePacket<PersistentStatus?, StatusUpdatePacket>(pokemon, value) {
    override val id = ID
    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeNullable(this.value) { pb, value -> pb.writeIdentifier(value.name) }
    }

    override fun set(pokemon: Pokemon, value: PersistentStatus?) {
        if (value == null) {
            pokemon.status = null
            return
        }
        pokemon.applyStatus(value)
    }

    companion object {
        val ID = cobblemonResource("status_update")
        fun decode(buffer: PacketByteBuf): StatusUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val identifier = buffer.readNullable(PacketByteBuf::readIdentifier) ?: return StatusUpdatePacket(pokemon, null)
            val status = Statuses.getStatus(identifier) as? PersistentStatus
            return StatusUpdatePacket(pokemon, status)
        }
    }
}