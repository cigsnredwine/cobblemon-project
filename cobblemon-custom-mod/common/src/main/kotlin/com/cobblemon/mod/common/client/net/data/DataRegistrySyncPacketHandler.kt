/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.data

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.client.data.DataRegistrySyncPacket
import net.minecraft.client.MinecraftClient

class DataRegistrySyncPacketHandler<P, T : DataRegistrySyncPacket<P, T>> : ClientNetworkPacketHandler<T> {
    override fun handle(packet: T, client: MinecraftClient) {
        packet.entries.clear()
        packet.entries.addAll(packet.buffer!!.readList(packet::decodeEntry).filterNotNull())
        packet.buffer!!.release()
        packet.synchronizeDecoded(packet.entries)
    }
}