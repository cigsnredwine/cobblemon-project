/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.callback

import com.cobblemon.mod.common.api.callback.MoveSelectDTO
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

/**
 * Packet send to the client to force them to open a move selection GUI.
 *
 * @author Hiroku
 * @since June 30th, 2023
 */
class OpenMoveCallbackPacket(val uuid: UUID, val title: MutableText, val moves: List<MoveSelectDTO>) : NetworkPacket<OpenMoveCallbackPacket> {
    companion object {
        val ID = cobblemonResource("open_move_callback")
        fun decode(buffer: PacketByteBuf) = OpenMoveCallbackPacket(
            uuid = buffer.readUuid(),
            title = buffer.readText().copy(),
            moves = buffer.readList { _ -> MoveSelectDTO(buffer) }
        )
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(uuid)
        buffer.writeText(title)
        buffer.writeCollection(moves) { _, v -> v.writeToBuffer(buffer) }
    }
}