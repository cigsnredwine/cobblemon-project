/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

class NicknameUpdatePacket(pokemon: () -> Pokemon, value: MutableText?): SingleUpdatePacket<MutableText?, NicknameUpdatePacket>(pokemon, value) {
    override val id = ID

    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeNullable(value) { _, v -> buffer.writeText(value) }
    }

    override fun set(pokemon: Pokemon, value: MutableText?) { pokemon.nickname = value }

    companion object {
        val ID = cobblemonResource("nickname_update")
        fun decode(buffer: PacketByteBuf): NicknameUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val nickname = buffer.readNullable { buffer.readText().copy() }
            return NicknameUpdatePacket(pokemon, nickname)
        }
    }

}