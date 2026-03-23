/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.trade

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.text.lightPurple
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.keybind.boundKey
import com.cobblemon.mod.common.client.keybind.keybinds.PartySendBinding
import com.cobblemon.mod.common.client.trade.ClientTradeOffer
import com.cobblemon.mod.common.net.messages.client.trade.TradeOfferNotificationPacket
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.MinecraftClient

object TradeOfferNotificationHandler : ClientNetworkPacketHandler<TradeOfferNotificationPacket> {
    override fun handle(packet: TradeOfferNotificationPacket, client: MinecraftClient) {
        CobblemonClient.requests.tradeOffers.add(ClientTradeOffer(packet.tradeOfferId, packet.traderId))
        client.player?.sendMessage(
            lang("trade.offer", packet.traderName, PartySendBinding.boundKey().localizedText),
            true
        )
    }
}