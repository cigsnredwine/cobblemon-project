/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.grower

import com.cobblemon.mod.common.api.apricorn.Apricorn
import com.cobblemon.mod.common.world.feature.CobblemonConfiguredFeatures
import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.random.Random

class ApricornTreeGrower(private val apricorn: Apricorn) : SaplingGenerator() {
    override fun getTreeFeature(random: Random, bl: Boolean) = when (this.apricorn) {
        Apricorn.BLACK -> CobblemonConfiguredFeatures.BLACK_APRICORN_TREE_KEY
        Apricorn.BLUE -> CobblemonConfiguredFeatures.BLUE_APRICORN_TREE_KEY
        Apricorn.GREEN -> CobblemonConfiguredFeatures.GREEN_APRICORN_TREE_KEY
        Apricorn.PINK -> CobblemonConfiguredFeatures.PINK_APRICORN_TREE_KEY
        Apricorn.RED -> CobblemonConfiguredFeatures.RED_APRICORN_TREE_KEY
        Apricorn.WHITE -> CobblemonConfiguredFeatures.WHITE_APRICORN_TREE_KEY
        Apricorn.YELLOW -> CobblemonConfiguredFeatures.YELLOW_APRICORN_TREE_KEY
    }
}