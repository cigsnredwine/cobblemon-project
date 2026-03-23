/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.multiblock.condition

import com.cobblemon.mod.common.util.blockPositionsAsList
import net.minecraft.predicate.BlockPredicate
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape

/**
 * Checks that a block adjacent to another block matches a predicate. Basically we find a block that
 * matched relToBlock, then we look in each direction to see if there is an adjacent block that matches
 * target block.
 *
 * @param relToBlock The [BlockPredicate] for the main block. The block that matches this predicate is the block whose adjacent blocks we check
 * @param targetBlock The [BlockPredicate] that is checked for blocks adjacent to relToBlock
 * @param directionsToCheck The [Direction]s that we check from the target relToBlock
 *
 *  @author Apion
 *  @since August 24th, 2023
 */
class BlockRelativeCondition(
    val relToBlock: BlockPredicate,
    val targetBlock: BlockPredicate,
    val directionsToCheck: Array<Direction> = Direction.values()
) : MultiblockCondition {
    override fun test(world: ServerWorld, box: VoxelShape): Boolean {
        val relToBlockBlockPositions = box.blockPositionsAsList().filter { relToBlock.test(world, it) }
        relToBlockBlockPositions.forEach { pos ->
            directionsToCheck.forEach {
                if (targetBlock.test(world, pos.offset(it))) {
                    return true
                }
            }
        }
        return false
    }

}
