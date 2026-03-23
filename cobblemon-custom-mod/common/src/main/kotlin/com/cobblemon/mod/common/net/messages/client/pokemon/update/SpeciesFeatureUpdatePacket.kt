/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatures
import com.cobblemon.mod.common.api.pokemon.feature.SynchronizedSpeciesFeature
import com.cobblemon.mod.common.api.pokemon.feature.SynchronizedSpeciesFeatureProvider
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Updates the state of a particular [SynchronizedSpeciesFeature].
 *
 * @author Hiroku
 * @since November 13th, 2023
 */
class SpeciesFeatureUpdatePacket(pokemon: () -> Pokemon, val species: Identifier, speciesFeature: SynchronizedSpeciesFeature) : SingleUpdatePacket<SynchronizedSpeciesFeature, SpeciesFeatureUpdatePacket>(pokemon, speciesFeature) {
    companion object {
        val ID = cobblemonResource("species_feature_update")
        fun decode(buffer: PacketByteBuf): SpeciesFeatureUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val speciesIdentifier = buffer.readIdentifier()
            val species = PokemonSpecies.getByIdentifier(speciesIdentifier)
                ?: throw IllegalStateException("Pokémon unable to be found during species feature update packet: $speciesIdentifier")
            val speciesFeatureName = buffer.readString()
            val featureProviders = SpeciesFeatures.getFeaturesFor(species).filterIsInstance<SynchronizedSpeciesFeatureProvider<*>>()
            val feature = featureProviders.firstNotNullOfOrNull { it(buffer, speciesFeatureName) } as? SynchronizedSpeciesFeature
                ?: throw IllegalArgumentException("Couldn't find a feature provider to deserialize this feature. Something's wrong.")
            return SpeciesFeatureUpdatePacket(pokemon, speciesIdentifier, feature)
        }
    }

    override val id = ID
    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeIdentifier(species)
        buffer.writeString(value.name)
        value.encode(buffer)
    }

    override fun set(pokemon: Pokemon, value: SynchronizedSpeciesFeature) {
        pokemon.features.removeIf { it.name == value.name }
        pokemon.features.add(value)
    }
}