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
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace

class AllowedRegionsSpawningInfluence : SpawningInfluence {
    override fun affectSpawnable(detail: SpawnDetail, ctx: SpawningContext): Boolean {
        val configuredRegions = BestSpawner.regionConfig.allowedRegionSet
        if (configuredRegions.isEmpty()) {
            return true
        }

        val pokemonDetail = detail as? PokemonSpawnDetail ?: return true
        val speciesId = pokemonDetail.pokemon.species ?: return true
        val species = PokemonSpecies.getByIdentifier(speciesId.asIdentifierDefaultingNamespace()) ?: return true
        val region = PokemonSpawnRegion.fromNationalDexNumber(species.nationalPokedexNumber) ?: return false
        return region in configuredRegions
    }
}
