/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.worldgen

import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraftforge.common.world.BiomeModifier
import net.minecraftforge.common.world.ModifiableBiomeInfo
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.server.ServerLifecycleHooks

/**
 * This class serves as a cheat to inject all our features via code instead of needing to use the Forge specific biome modifications system.
 *
 * @author Licious
 * @since February 12th, 2023
 */
internal object CobblemonBiomeModifiers : BiomeModifier {

    private var codec: Codec<out BiomeModifier>? = null
    private val entries = arrayListOf<Entry>()

    fun register(event: RegisterEvent) {
        event.register(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS) { helper ->
            this.codec = Codec.unit(CobblemonBiomeModifiers)
            helper.register(cobblemonResource("inject_coded"), this.codec)
        }
    }

    fun add(feature: RegistryKey<PlacedFeature>, step: GenerationStep.Feature, validTag: TagKey<Biome>?) {
        this.entries += Entry(feature, step, validTag)
    }

    override fun modify(arg: RegistryEntry<Biome>, phase: BiomeModifier.Phase, builder: ModifiableBiomeInfo.BiomeInfo.Builder) {
        if (phase != BiomeModifier.Phase.ADD) {
            return
        }
        val server = ServerLifecycleHooks.getCurrentServer()
        val registry = server.registryManager.get(RegistryKeys.PLACED_FEATURE)
        this.entries.forEach { entry ->
            if (entry.validTag == null || arg.isIn(entry.validTag)) {
                builder.generationSettings.feature(entry.step, RegistryEntry.of(registry.get(entry.feature)))
            }
        }
    }

    override fun codec(): Codec<out BiomeModifier> = this.codec ?: Codec.unit(CobblemonBiomeModifiers)

    private data class Entry(val feature: RegistryKey<PlacedFeature>, val step: GenerationStep.Feature, val validTag: TagKey<Biome>?)

}