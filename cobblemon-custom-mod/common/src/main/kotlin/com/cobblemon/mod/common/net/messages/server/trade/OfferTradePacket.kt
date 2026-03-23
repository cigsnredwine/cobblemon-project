/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.trade

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

class OfferTradePacket(val offeredPlayerId: UUID) : NetworkPacket<OfferTradePacket> {
    companion object {
        val ID = cobblemonResource("offer_trade")
        fun decode(buffer: PacketByteBuf) = OfferTradePacket(buffer.readUuid())
    }
    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(offeredPlayerId)
    }
}