/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.tags.CobblemonBiomeTags
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.BiomeTags
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures

object CobblemonPlacedFeatures {

    // TODO we don't need a placed feature for every colour, clean all this crap in the JSONs (ask Hiro)
    val BLACK_APRICORN_TREE_PLACED_FEATURE = of("black_apricorn_tree")
    val BLUE_APRICORN_TREE_PLACED_FEATURE = of("blue_apricorn_tree")
    val GREEN_APRICORN_TREE_PLACED_FEATURE = of("green_apricorn_tree")
    val PINK_APRICORN_TREE_PLACED_FEATURE = of("pink_apricorn_tree")
    val RED_APRICORN_TREE_PLACED_FEATURE = of("red_apricorn_tree")
    val WHITE_APRICORN_TREE_PLACED_FEATURE = of("white_apricorn_tree")
    val YELLOW_APRICORN_TREE_PLACED_FEATURE = of("yellow_apricorn_tree")


    val APRICORN_TREES = of("apricorn_trees")

    val MINTS = of("mints")

    val MEDICINAL_LEEK = of("medicinal_leek")
    val BIG_ROOT = of("big_root")

    val REVIVAL_HERB = of("revival_herb")

    val BERRY_GROVE = of("berry_groves")

    fun register() {
        // We don't need to pass in any tags, the feature implementation handles it, while not a perfect system it works
        Cobblemon.implementation.addFeatureToWorldGen(APRICORN_TREES, GenerationStep.Feature.VEGETAL_DECORATION, null)
        Cobblemon.implementation.addFeatureToWorldGen(MINTS, GenerationStep.Feature.VEGETAL_DECORATION, null)
        Cobblemon.implementation.addFeatureToWorldGen(MEDICINAL_LEEK, GenerationStep.Feature.VEGETAL_DECORATION, null)
        Cobblemon.implementation.addFeatureToWorldGen(BIG_ROOT, GenerationStep.Feature.VEGETAL_DECORATION, BiomeTags.IS_OVERWORLD)
        Cobblemon.implementation.addFeatureToWorldGen(REVIVAL_HERB, GenerationStep.Feature.VEGETAL_DECORATION, CobblemonBiomeTags.HAS_REVIVAL_HERBS)
        Cobblemon.implementation.addFeatureToWorldGen(BERRY_GROVE, GenerationStep.Feature.VEGETAL_DECORATION, BiomeTags.IS_OVERWORLD)
    }

    private fun of(id: String): RegistryKey<PlacedFeature> = PlacedFeatures.of("${Cobblemon.MODID}:$id")
}
