/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.ui

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.net.messages.PokemonDTO
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class SummaryUIPacket internal constructor(val pokemon: List<PokemonDTO>, val editable: Boolean): NetworkPacket<SummaryUIPacket> {

    override val id = ID

    constructor(vararg pokemon: Pokemon, editable: Boolean = true) : this(pokemon.map { PokemonDTO(it, true) }, editable)

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(editable)
        buffer.writeCollection(this.pokemon) { pb, value -> value.encode(pb) }
    }

    companion object {
        val ID = cobblemonResource("summary_ui")
        fun decode(buffer: PacketByteBuf) = SummaryUIPacket(buffer.readList { PokemonDTO().apply { decode(it) } }, buffer.readBoolean())
    }
}