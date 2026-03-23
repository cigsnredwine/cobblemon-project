/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.battle

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text

/**
 * Sends messages to add to the battle message queue on the client.
 *
 * @author Hiroku
 * @since May 22nd, 2022
 */
class BattleMessagePacket(val messages: List<Text>) : NetworkPacket<BattleMessagePacket> {

    override val id = ID

    constructor(vararg messages: Text): this(messages.toList())

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeCollection(this.messages) { pb, value -> pb.writeText(value) }
    }

    companion object {
        val ID = cobblemonResource("battle_message")
        fun decode(buffer: PacketByteBuf) = BattleMessagePacket(buffer.readList { it.readText() })
    }
}