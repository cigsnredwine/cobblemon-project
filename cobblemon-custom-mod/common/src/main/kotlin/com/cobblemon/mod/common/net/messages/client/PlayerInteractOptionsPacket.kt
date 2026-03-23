/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import java.util.UUID
import java.util.EnumSet

/**
 * Used to populate the player interaction menu
 *
 *
 * @author Apion
 * @since November 5th, 2023
 */
class PlayerInteractOptionsPacket(
    val options: EnumSet<Options>,
    val targetId: UUID,
    val numericTargetId: Int,
    val selectedPokemonId: UUID
    ) : NetworkPacket<PlayerInteractOptionsPacket> {
    companion object {
        val ID = cobblemonResource("player_interactions")
        fun decode(buffer: PacketByteBuf) = PlayerInteractOptionsPacket(
            buffer.readEnumSet(Options::class.java),
            buffer.readUuid(),
            buffer.readInt(),
            buffer.readUuid()
        )
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeEnumSet(options, Options::class.java)
        buffer.writeUuid(targetId)
        buffer.writeInt(numericTargetId)
        buffer.writeUuid(selectedPokemonId)
    }

    enum class Options {
        BATTLE,
        SPECTATE_BATTLE,
        TRADE
    }

}