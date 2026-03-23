/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.pasture

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.pasture.PastureLinkManager
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.net.messages.client.pasture.ClosePasturePacket
import com.cobblemon.mod.common.net.messages.client.pasture.PokemonUnpasturedPacket
import com.cobblemon.mod.common.net.messages.server.pasture.UnpastureAllPokemonPacket
import com.cobblemon.mod.common.net.messages.server.pasture.UnpasturePokemonPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object UnpastureAllPokemonHandler : ServerNetworkPacketHandler<UnpastureAllPokemonPacket> {
    override fun handle(packet: UnpastureAllPokemonPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pastureLink = PastureLinkManager.getLinkByPlayer(player) ?: return player.sendPacket(ClosePasturePacket())
        val pastureBlockEntity = player.world.getBlockEntity(pastureLink.pos) as? PokemonPastureBlockEntity ?: return
        pastureBlockEntity.releaseAllPokemon(player.uuid).map(::PokemonUnpasturedPacket).forEach { it.sendToPlayer(player) }
    }
}