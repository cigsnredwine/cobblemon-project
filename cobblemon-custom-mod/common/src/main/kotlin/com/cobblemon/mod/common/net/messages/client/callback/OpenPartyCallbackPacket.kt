/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.callback

import com.cobblemon.mod.common.api.callback.PartySelectPokemonDTO
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

/**
 * Packet send to the client to force them to open a party selection GUI.
 *
 * @author Hiroku
 * @since July 7th, 2023
 */
class OpenPartyCallbackPacket(
    val uuid: UUID,
    val title: MutableText,
//    val usePortraits: Boolean,
//    val animate: Boolean,
    val pokemon: List<PartySelectPokemonDTO>
) : NetworkPacket<OpenPartyCallbackPacket> {
    companion object {
        val ID = cobblemonResource("open_party_callback")
        fun decode(buffer: PacketByteBuf) = OpenPartyCallbackPacket(
            uuid = buffer.readUuid(),
            title = buffer.readText().copy(),
//            usePortraits = buffer.readBoolean(),
//            animate = buffer.readBoolean(),
            pokemon = buffer.readList { _ -> PartySelectPokemonDTO(buffer) }
        )
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(uuid)
        buffer.writeText(title)
//        buffer.writeBoolean(usePortraits)
//        buffer.writeBoolean(animate)
        buffer.writeCollection(pokemon) { _, v -> v.writeToBuffer(buffer) }
    }
}