/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.callback.move

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent to the server when the player has responded to a move selection callback.
 *
 * @author Hiroku
 * @since June 30th, 2023
 */
class MoveSelectedPacket(val uuid: UUID, val index: Int) : NetworkPacket<MoveSelectedPacket> {
    companion object {
        val ID = cobblemonResource("move_selected")
        fun decode(buffer: PacketByteBuf) = MoveSelectedPacket(buffer.readUuid(), buffer.readSizedInt(IntSize.U_BYTE))
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(uuid)
        buffer.writeSizedInt(IntSize.U_BYTE, index)
    }
}