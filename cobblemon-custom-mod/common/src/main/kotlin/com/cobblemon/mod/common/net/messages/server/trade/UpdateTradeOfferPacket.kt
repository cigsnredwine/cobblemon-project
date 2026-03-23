/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.trade

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.storage.StorePosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

class UpdateTradeOfferPacket(val newOffer: Pair<UUID, PartyPosition>?): NetworkPacket<UpdateTradeOfferPacket> {
    companion object {
        val ID = cobblemonResource("update_trade_offer")
        fun decode(buffer: PacketByteBuf) = UpdateTradeOfferPacket(buffer.readNullable { buffer.readUuid() to buffer.readPartyPosition() })
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeNullable(newOffer) { buffer, (pokemonId, partyPosition) ->
            buffer.writeUuid(pokemonId)
            buffer.writePartyPosition(partyPosition)
        }
    }
}