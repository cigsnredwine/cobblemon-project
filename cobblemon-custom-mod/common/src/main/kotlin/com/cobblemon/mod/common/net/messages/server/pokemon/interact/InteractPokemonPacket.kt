/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.pokemon.interact

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.net.serverhandling.pokemon.interact.InteractPokemonHandler
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Tells the server to handle Pokémon interaction.
 *
 * Handled by [InteractPokemonHandler].
 *
 * @author Village
 * @since January 7th, 2023
 */
class InteractPokemonPacket(val pokemonID: UUID, val mountShoulder: Boolean) : NetworkPacket<InteractPokemonPacket> {
    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pokemonID)
        buffer.writeBoolean(mountShoulder)
    }
    companion object {
        val ID = cobblemonResource("interact_pokemon")
        fun decode(buffer: PacketByteBuf) = InteractPokemonPacket(buffer.readUuid(), buffer.readBoolean())
    }
}