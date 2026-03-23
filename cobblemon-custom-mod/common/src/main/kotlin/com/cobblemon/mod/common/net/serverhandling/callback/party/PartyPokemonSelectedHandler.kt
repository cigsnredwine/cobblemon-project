/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.callback.party

import com.cobblemon.mod.common.api.callback.PartySelectCallbacks
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.server.callback.party.PartyPokemonSelectedPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object PartyPokemonSelectedHandler : ServerNetworkPacketHandler<PartyPokemonSelectedPacket> {
    override fun handle(packet: PartyPokemonSelectedPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        PartySelectCallbacks.handleCallback(player, packet.uuid, packet.index)
    }
}