/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.net

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

interface ServerNetworkPacketHandler<T: NetworkPacket<T>> {

    fun handle(packet: T, server: MinecraftServer, player: ServerPlayerEntity)

    fun handleOnNettyThread(packet: T, server: MinecraftServer, player: ServerPlayerEntity) {
        server.execute { handle(packet, server, player) }
    }
}