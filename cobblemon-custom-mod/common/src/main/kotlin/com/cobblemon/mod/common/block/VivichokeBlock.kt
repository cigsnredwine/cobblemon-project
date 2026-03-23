/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonItems
import net.minecraft.block.BlockState
import net.minecraft.block.CropBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemConvertible
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class VivichokeBlock(settings: Settings) : CropBlock(settings) {

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = AGE_TO_SHAPE.getOrElse(state.get(this.ageProperty)) { VoxelShapes.fullCube() }

    // This is a design choice, they shouldn't grow more than a single stage at a time.
    override fun getGrowthAmount(world: World): Int = 1

    override fun getSeedsItem(): ItemConvertible = CobblemonItems.VIVICHOKE_SEEDS

    companion object {

        private val STAGE_0_SHAPE = createCuboidShape(6.0, -1.0, 6.0, 10.0, 2.0, 10.0)
        private val STAGE_1_SHAPE = createCuboidShape(6.0, -1.0, 6.0, 10.0, 5.0, 10.0)
        private val STAGE_2_SHAPE = createCuboidShape(6.0, -1.0, 6.0, 10.0, 7.0, 10.0)
        private val STAGE_3_SHAPE = createCuboidShape(6.0, -1.0, 6.0, 10.0, 9.0, 10.0)
        private val STAGE_4_SHAPE = createCuboidShape(6.0, -1.0, 6.0, 10.0, 7.0, 10.0)
        private val STAGE_5_SHAPE = createCuboidShape(6.0, -1.0, 6.0, 10.0, 8.0, 10.0)
        private val STAGE_6_SHAPE = createCuboidShape(6.0, -1.0, 6.0, 10.0, 9.0, 10.0)
        private val STAGE_7_SHAPE = VoxelShapes.union(
            createCuboidShape(6.0, -1.0, 6.0, 10.0, 10.0, 10.0),
            createCuboidShape(5.5, 10.0, 5.5, 10.5, 14.0, 10.5)
        )

        /**
         * An array with the equivalent [VoxelShape] of a growth stage as the index.
         */
        val AGE_TO_SHAPE = arrayOf(
            STAGE_0_SHAPE,
            STAGE_1_SHAPE,
            STAGE_2_SHAPE,
            STAGE_3_SHAPE,
            STAGE_4_SHAPE,
            STAGE_5_SHAPE,
            STAGE_6_SHAPE,
            STAGE_7_SHAPE
        )

    }
}