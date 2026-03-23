/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.apricorn

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.block.ApricornBlock
import com.cobblemon.mod.common.block.ApricornSaplingBlock
import com.cobblemon.mod.common.item.ApricornItem
import net.minecraft.block.MapColor
import net.minecraft.item.BlockItem

/**
 * Contains the different Apricorn variants and util methods to get their item, block, seedling and sapling form.
 *
 * @author Licious
 * @since October 29th, 2022
 */
enum class Apricorn {

    BLACK,
    BLUE,
    GREEN,
    PINK,
    RED,
    WHITE,
    YELLOW;

    fun item(): ApricornItem = when(this) {
        BLACK -> CobblemonItems.BLACK_APRICORN
        BLUE -> CobblemonItems.BLUE_APRICORN
        GREEN -> CobblemonItems.GREEN_APRICORN
        PINK -> CobblemonItems.PINK_APRICORN
        RED -> CobblemonItems.RED_APRICORN
        WHITE -> CobblemonItems.WHITE_APRICORN
        YELLOW -> CobblemonItems.YELLOW_APRICORN
    }

    fun seed(): BlockItem = when(this) {
        BLACK -> CobblemonItems.BLACK_APRICORN_SEED
        BLUE -> CobblemonItems.BLUE_APRICORN_SEED
        GREEN -> CobblemonItems.GREEN_APRICORN_SEED
        PINK -> CobblemonItems.PINK_APRICORN_SEED
        RED -> CobblemonItems.RED_APRICORN_SEED
        WHITE -> CobblemonItems.WHITE_APRICORN_SEED
        YELLOW -> CobblemonItems.YELLOW_APRICORN_SEED
    }

    fun block(): ApricornBlock = when(this) {
        BLACK -> CobblemonBlocks.BLACK_APRICORN
        BLUE -> CobblemonBlocks.BLUE_APRICORN
        GREEN -> CobblemonBlocks.GREEN_APRICORN
        PINK -> CobblemonBlocks.PINK_APRICORN
        RED -> CobblemonBlocks.RED_APRICORN
        WHITE -> CobblemonBlocks.WHITE_APRICORN
        YELLOW -> CobblemonBlocks.YELLOW_APRICORN
    }

    fun sapling(): ApricornSaplingBlock = when(this) {
        BLACK -> CobblemonBlocks.BLACK_APRICORN_SAPLING
        BLUE -> CobblemonBlocks.BLUE_APRICORN_SAPLING
        GREEN -> CobblemonBlocks.GREEN_APRICORN_SAPLING
        PINK -> CobblemonBlocks.PINK_APRICORN_SAPLING
        RED -> CobblemonBlocks.RED_APRICORN_SAPLING
        WHITE -> CobblemonBlocks.WHITE_APRICORN_SAPLING
        YELLOW -> CobblemonBlocks.YELLOW_APRICORN_SAPLING
    }

    fun mapColor(): MapColor = when(this) {
        BLACK -> MapColor.BLACK
        BLUE ->  MapColor.BLUE
        GREEN ->  MapColor.GREEN
        PINK ->  MapColor.PINK
        RED ->  MapColor.RED
        WHITE ->  MapColor.WHITE
        YELLOW ->  MapColor.YELLOW
    }

}