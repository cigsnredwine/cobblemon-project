/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.sherds

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

@Suppress("Unused")
object CobblemonSherds {
    val allSherds = mutableListOf<CobblemonSherd>()
    val sherdToPattern = mutableMapOf<Item, RegistryKey<String>>()

    val BYGONE_SHERD = addSherd(cobblemonResource("bygone_pottery_pattern"), CobblemonItems.BYGONE_SHERD)

    val CAPTURE_SHERD = addSherd(cobblemonResource("capture_pottery_pattern"), CobblemonItems.CAPTURE_SHERD)

    val DOME_SHERD = addSherd(cobblemonResource("dome_pottery_pattern"), CobblemonItems.DOME_SHERD)

    val HELIX_SHERD = addSherd(cobblemonResource("helix_pottery_pattern"), CobblemonItems.HELIX_SHERD)

    val NOSTALGIC_SHERD = addSherd(cobblemonResource("nostalgic_pottery_pattern"), CobblemonItems.NOSTALGIC_SHERD)

    val SUSPICIOUS_SHERD = addSherd(cobblemonResource("suspicious_pottery_pattern"), CobblemonItems.SUSPICIOUS_SHERD)

    fun addSherd(patternId: Identifier, item: Item): CobblemonSherd {
        val sherd = CobblemonSherd(patternId, item)
        val registryKey = RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERN, patternId)
        sherdToPattern[item] = registryKey
        allSherds.add(sherd)
        return sherd
    }
    fun registerSherds() {
        val registry = Registries.DECORATED_POT_PATTERN
        for (sherd in allSherds) {
            val regKey = RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERN, sherd.patternId)
            Registry.register(
                registry,
                regKey,
                sherd.patternId.path
            )
        }
    }

}