/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonItems
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

@Suppress("OVERRIDE_DEPRECATION")
class EnergyRootBlock(settings: Settings) : RootBlock(settings) {

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = AABB

    override fun shearedResultingState(): BlockState = CobblemonBlocks.BIG_ROOT.defaultState

    override fun shearedDrop(): ItemStack = CobblemonItems.ENERGY_ROOT.defaultStack

    companion object {

        private val AABB = VoxelShapes.cuboid(0.2, 0.1, 0.2, 0.8, 1.0, 0.8)

    }

}