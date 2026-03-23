/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.preset

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.api.spawning.PokemonSpawnRegion
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.InputStreamReader
import kotlin.math.floor
import kotlin.random.Random

class PokemonSpawnRateConfig {
    val version = 1
    val replaceWithNewVersion = true
    val enabled = true
    val spawnAttemptsMultiplier = 1.0F
    val regionWeightMultipliers = mutableMapOf<String, Float>()
    val biomeWeightMultipliers = mutableMapOf<String, Float>()
    val speciesWeightMultipliers = mutableMapOf<String, Float>()

    fun getSpawnAttemptsPerCycle(random: Random = Random.Default): Int {
        if (!enabled) {
            return 1
        }

        val multiplier = spawnAttemptsMultiplier.coerceAtLeast(0F)
        val wholeAttempts = floor(multiplier).toInt()
        val fractional = multiplier - wholeAttempts
        val extraAttempt = if (fractional > 0F && random.nextFloat() < fractional) 1 else 0
        return wholeAttempts + extraAttempt
    }

    fun getSpeciesWeightMultiplier(speciesId: String?): Float {
        if (!enabled || speciesId == null) {
            return 1F
        }

        val normalizedId = speciesId.asIdentifierDefaultingNamespace().toString().lowercase()
        val namespacedMultiplier = speciesWeightMultipliers[normalizedId]
        if (namespacedMultiplier != null) {
            return namespacedMultiplier
        }

        val pathMultiplier = speciesWeightMultipliers[normalizedId.substringAfter(':')]
        return pathMultiplier ?: 1F
    }

    fun getBiomeWeightMultiplier(biomeId: String?): Float {
        if (!enabled || biomeId == null) {
            return 1F
        }

        val normalizedId = biomeId.asIdentifierDefaultingNamespace("minecraft").toString().lowercase()
        val namespacedMultiplier = biomeWeightMultipliers[normalizedId]
        if (namespacedMultiplier != null) {
            return namespacedMultiplier
        }

        val pathMultiplier = biomeWeightMultipliers[normalizedId.substringAfter(':')]
        return pathMultiplier ?: 1F
    }

    fun getRegionWeightMultiplier(region: PokemonSpawnRegion?): Float {
        if (!enabled || region == null) {
            return 1F
        }

        return regionWeightMultipliers[region.id] ?: 1F
    }

    companion object {
        val GSON = GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        const val CONFIG_NAME = "pokemon-spawn-rate-config"

        fun load(): PokemonSpawnRateConfig {
            val internal = loadInternal()
            val external = loadExternal()
            return if (external == null) {
                saveExternal()
                internal
            } else if (external.replaceWithNewVersion && internal.version > external.version) {
                saveExternal()
                internal
            } else {
                external
            }
        }

        private fun loadInternal(): PokemonSpawnRateConfig {
            val reader = InputStreamReader(Cobblemon::class.java.getResourceAsStream("/assets/${Cobblemon.MODID}/spawning/$CONFIG_NAME.json")!!)
            val config = GSON.fromJson(reader, PokemonSpawnRateConfig::class.java)
            reader.close()
            return config
        }

        private fun loadExternal(): PokemonSpawnRateConfig? {
            val configFile = File("config/${Cobblemon.MODID}/spawning/$CONFIG_NAME.json")
            configFile.parentFile.mkdirs()
            return if (configFile.exists()) {
                try {
                    val reader = FileReader(configFile)
                    val config = GSON.fromJson(reader, PokemonSpawnRateConfig::class.java)
                    reader.close()
                    config
                } catch (e: Exception) {
                    LOGGER.error("Unable to load external Pokemon spawn rate configuration", e)
                    null
                }
            } else {
                null
            }
        }

        fun saveExternal() {
            val stream = Cobblemon::class.java.getResourceAsStream("/assets/${Cobblemon.MODID}/spawning/$CONFIG_NAME.json")!!
            val bytes = stream.readAllBytes()
            stream.close()
            val configFile = File("config/${Cobblemon.MODID}/spawning/$CONFIG_NAME.json")
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            val outputStream = FileOutputStream(configFile)
            outputStream.write(bytes)
            outputStream.close()
        }
    }
}
