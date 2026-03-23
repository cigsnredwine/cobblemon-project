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
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.InputStreamReader

class PokemonSpawnRegionConfig {
    val version = 1
    val replaceWithNewVersion = true
    val enabled = true
    val allowedRegions = mutableListOf("kanto", "johto")
    val availableRegions = PokemonSpawnRegion.ids

    val allowedRegionSet: Set<PokemonSpawnRegion>
        get() = if (!enabled) {
            emptySet()
        } else {
            allowedRegions.mapNotNull(PokemonSpawnRegion::fromId).toSet()
        }

    companion object {
        val GSON = GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        const val CONFIG_NAME = "pokemon-spawn-region-config"

        fun load(): PokemonSpawnRegionConfig {
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

        private fun loadInternal(): PokemonSpawnRegionConfig {
            val reader = InputStreamReader(Cobblemon::class.java.getResourceAsStream("/assets/${Cobblemon.MODID}/spawning/$CONFIG_NAME.json")!!)
            val config = GSON.fromJson(reader, PokemonSpawnRegionConfig::class.java)
            reader.close()
            return config
        }

        private fun loadExternal(): PokemonSpawnRegionConfig? {
            val configFile = File("config/${Cobblemon.MODID}/spawning/$CONFIG_NAME.json")
            configFile.parentFile.mkdirs()
            return if (configFile.exists()) {
                try {
                    val reader = FileReader(configFile)
                    val config = GSON.fromJson(reader, PokemonSpawnRegionConfig::class.java)
                    reader.close()
                    config
                } catch (e: Exception) {
                    LOGGER.error("Unable to load external Pokemon spawn region configuration", e)
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
