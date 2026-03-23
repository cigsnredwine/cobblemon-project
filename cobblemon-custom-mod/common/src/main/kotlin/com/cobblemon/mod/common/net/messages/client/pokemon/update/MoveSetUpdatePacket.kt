/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.moves.MoveSet
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class MoveSetUpdatePacket(pokemon: () -> Pokemon, value: MoveSet) : SingleUpdatePacket<MoveSet, MoveSetUpdatePacket>(pokemon, value) {
    override val id = ID
    override fun encodeValue(buffer: PacketByteBuf) {
        this.value.saveToBuffer(buffer)
    }

    override fun set(pokemon: Pokemon, value: MoveSet) {
        pokemon.moveSet.copyFrom(value)
    }
    companion object {
        val ID = cobblemonResource("moveset_update")
        fun decode(buffer: PacketByteBuf) = MoveSetUpdatePacket(decodePokemon(buffer), MoveSet().apply { loadFromBuffer(buffer) })
    }
}