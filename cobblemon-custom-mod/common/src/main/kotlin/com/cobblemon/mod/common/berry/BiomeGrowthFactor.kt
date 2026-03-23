/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.berry

import com.cobblemon.mod.common.api.berry.GrowthFactor
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.registry.BiomeIdentifierCondition
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.block.BlockState
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView
import net.minecraft.world.biome.Biome

class PreferredBiomeGrowthFactor(
    val bonusYield: IntRange
) : GrowthFactor{
    override fun validateArguments() {
        if (this.bonusYield.first < 0 || this.bonusYield.last < 0) {
            throw IllegalArgumentException("$ID bonusYield must be a positive range")
        }
    }

    override fun isValid(world: WorldView, state: BlockState, pos: BlockPos): Boolean {
        val biome = world.getBiome(pos)
        val block = state.block as BerryBlock
        val biomeTags = block.berry()?.preferredBiomeTags ?: emptyList()

        return biomeTags.any { biome.isIn(it) }
    }

    override fun yield() = this.bonusYield.random()

    override fun minYield() = this.bonusYield.first

    override fun maxYield() = this.bonusYield.last

    companion object {

        val ID = cobblemonResource("preferred_biome")

    }

}
