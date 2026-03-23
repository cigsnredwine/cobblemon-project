/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.storage.pc

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.pc.PCGUI
import com.cobblemon.mod.common.client.gui.pc.PCGUIConfiguration
import com.cobblemon.mod.common.net.messages.client.storage.pc.OpenPCPacket
import net.minecraft.client.MinecraftClient

object OpenPCHandler : ClientNetworkPacketHandler<OpenPCPacket> {
    override fun handle(packet: OpenPCPacket, client: MinecraftClient) {
        val pc = CobblemonClient.storage.pcStores[packet.storeID] ?: return
        MinecraftClient.getInstance().setScreen(PCGUI(pc, CobblemonClient.storage.myParty, PCGUIConfiguration()))
    }
}