/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.berry.spawncondition

import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.math.random.Random

import net.minecraft.world.biome.Biome

class AllBiomeCondition(val minGroveSize: Int, val maxGroveSize: Int) : BerrySpawnCondition{
    override fun canSpawn(berry: Berry, biome: RegistryEntry<Biome>): Boolean {
        return true
    }

    override fun getGroveSize(random: Random): Int {
        return random.nextBetween(minGroveSize, maxGroveSize)
    }

    companion object {
        val ID = cobblemonResource("all_biome")
    }
}
