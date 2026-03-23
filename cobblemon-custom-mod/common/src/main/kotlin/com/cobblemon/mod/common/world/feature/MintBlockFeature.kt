/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature

import com.cobblemon.mod.common.block.MintBlock
import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.SingleStateFeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext

class MintBlockFeature : Feature<SingleStateFeatureConfig>(SingleStateFeatureConfig.CODEC) {

    override fun generate(context: FeatureContext<SingleStateFeatureConfig>): Boolean {
        val world = context.world
        val blockPos = context.origin
        val blockState = context.config.state
        val floor = blockPos.down()

        if (!world.getBlockState(floor).isIn(BlockTags.DIRT)) return false

        // Attempt to get at least one other valid position for the crop
        val validPlacements = getValidPositions(world, blockPos)
        if (validPlacements.isEmpty()) return false

        val minAge = MintBlock.MATURE_AGE - 2
        val maxAge = MintBlock.MATURE_AGE

        // Generate the blocks
        world.setBlockState(blockPos, blockState.with(MintBlock.AGE, context.random.nextBetween(minAge, maxAge)), Block.NOTIFY_LISTENERS)
        validPlacements.shuffled().take(2).forEach { position ->
            world.setBlockState(position, blockState.with(MintBlock.AGE, context.random.nextBetween(minAge, maxAge)), Block.NOTIFY_LISTENERS)
        }
        return true
    }

    private fun getValidPositions(world: StructureWorldAccess, origin: BlockPos): List<BlockPos> {
        val validPositions = mutableListOf<BlockPos>()

        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    if (x == 0 && z == 0) continue
                    val offsetPos = origin.add(x, y, z)
                    val floorBlockState = world.getBlockState(offsetPos.down())
                    if (world.isAir(offsetPos) && floorBlockState.isIn(BlockTags.DIRT)) {
                        validPositions.add(offsetPos)
                    }
                }
            }
        }

        return validPositions
    }

}