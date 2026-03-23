/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.entity.LivingEntity
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.structure.Structure

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain structure.
 *
 * @property structureCondition The [Identifier] of the structure the queried entity is expected to be in.
 * @author whatsy
 * @since December 5th, 2023
 */
class StructureRequirement : EntityQueryRequirement {
    val structureCondition: RegistryLikeCondition<Structure>? = null
    val structureAnticondition: RegistryLikeCondition<Structure>? = null
    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        val structures = queriedEntity.world.getChunk(queriedEntity.blockPos).structureReferences
        val registry = queriedEntity.world.registryManager.get(RegistryKeys.STRUCTURE)
        return (structureCondition == null || structures.any { structureCondition.fits(it.key, registry) }) && (structureAnticondition == null || !structures.any { structureAnticondition.fits(it.key, registry) })
    }

    companion object {
        const val ADAPTER_VARIANT = "structure"
    }

}