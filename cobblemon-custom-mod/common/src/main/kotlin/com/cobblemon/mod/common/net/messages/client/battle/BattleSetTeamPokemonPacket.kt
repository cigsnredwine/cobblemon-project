/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.battle

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.net.messages.PokemonDTO
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf


/**
 * Gives the client the true details of their team in the battle. This is so that switch choices can be made with
 * full details.
 *
 * Handled by [com.cobblemon.mod.common.client.net.battle.BattleSetTeamPokemonHandler].
 *
 * @author Hiroku
 * @since June 6th, 2022
 */
class BattleSetTeamPokemonPacket(val team: List<PokemonDTO>) : NetworkPacket<BattleSetTeamPokemonPacket> {

    override val id = ID

    constructor(team: Collection<Pokemon>) : this(team.map { PokemonDTO(it, true) })

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeCollection(this.team) { pb, value -> value.encode(pb) }
    }
    companion object {
        val ID = cobblemonResource("battle_set_team")
        fun decode(buffer: PacketByteBuf) = BattleSetTeamPokemonPacket(buffer.readList { PokemonDTO().apply { decode(it) } })
    }
}