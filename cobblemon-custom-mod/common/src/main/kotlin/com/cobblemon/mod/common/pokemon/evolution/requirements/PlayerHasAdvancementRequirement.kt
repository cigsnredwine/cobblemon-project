/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.advancement.Advancement
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * An [EvolutionRequirement] that checks if the player has a certain [Advancement]
 *
 * @param requiredAdvancement The [Identifier] of the required advancement
 *
 * @author whatsy
 */
class PlayerHasAdvancementRequirement(val requiredAdvancement: Identifier) : EntityQueryRequirement {
    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        val player = queriedEntity as? ServerPlayerEntity ?: return false
        for (entry in player.advancementTracker.progress) {
            if (entry.key.id == requiredAdvancement && entry.value.isDone) {
                return true
            }
        }
        return false
    }

    companion object {
        val ADAPTER_VARIANT = "advancement"
    }
}