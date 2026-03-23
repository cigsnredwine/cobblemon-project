/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.sound

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.client.sound.UnvalidatedPlaySoundS2CPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundEvent

internal object UnvalidatedPlaySoundS2CPacketHandler : ClientNetworkPacketHandler<UnvalidatedPlaySoundS2CPacket> {
    override fun handle(packet: UnvalidatedPlaySoundS2CPacket, client: MinecraftClient) {
        client.executeSync {
            // This prevents clients that might not have the required resources installed from getting spammed about missing sound
            if (client.soundManager.get(packet.sound) != null) {
                client.world?.playSound(client.player, packet.x, packet.y, packet.z, SoundEvent.of(packet.sound), packet.category, packet.volume, packet.pitch)
            }
        }
    }
}