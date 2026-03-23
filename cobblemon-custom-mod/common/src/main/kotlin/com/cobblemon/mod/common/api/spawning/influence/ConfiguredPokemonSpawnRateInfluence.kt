/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.influence

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.BestSpawner
import com.cobblemon.mod.common.api.spawning.PokemonSpawnRegion
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace

class ConfiguredPokemonSpawnRateInfluence : SpawningInfluence {
    override fun affectWeight(detail: SpawnDetail, ctx: SpawningContext, weight: Float): Float {
        val pokemonDetail = detail as? PokemonSpawnDetail ?: return weight
        val speciesId = pokemonDetail.pokemon.species
        val region = speciesId
            ?.let { PokemonSpecies.getByIdentifier(it.asIdentifierDefaultingNamespace()) }
            ?.let { PokemonSpawnRegion.fromNationalDexNumber(it.nationalPokedexNumber) }
        val regionMultiplier = BestSpawner.spawnRateConfig.getRegionWeightMultiplier(region)
        val biomeMultiplier = BestSpawner.spawnRateConfig.getBiomeWeightMultiplier(ctx.biomeName.toString())
        val speciesMultiplier = BestSpawner.spawnRateConfig.getSpeciesWeightMultiplier(speciesId)
        return weight * regionMultiplier * biomeMultiplier * speciesMultiplier
    }
}
