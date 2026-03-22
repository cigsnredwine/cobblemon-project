/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning

enum class PokemonSpawnRegion(
    val id: String,
    val nationalDexRange: IntRange,
    private vararg val aliases: String
) {
    KANTO("kanto", 1..151),
    JOHTO("johto", 152..251),
    HOENN("hoenn", 252..386),
    SINNOH("sinnoh", 387..493),
    UNOVA("unova", 494..649),
    KALOS("kalos", 650..721),
    ALOLA("alola", 722..809),
    GALAR("galar", 810..898),
    HISUI("hisui", 899..905),
    PALDEA("paldea", 906..1025);

    fun matches(nationalDexNumber: Int): Boolean {
        return nationalDexNumber in nationalDexRange
    }

    companion object {
        private val all = values().toList()
        val ids = all.map(PokemonSpawnRegion::id)

        fun fromId(id: String): PokemonSpawnRegion? {
            val normalized = id.trim().lowercase()
            return all.firstOrNull { region ->
                normalized == region.id || normalized in region.aliases
            }
        }

        fun fromNationalDexNumber(nationalDexNumber: Int): PokemonSpawnRegion? {
            return all.firstOrNull { it.matches(nationalDexNumber) }
        }
    }
}
