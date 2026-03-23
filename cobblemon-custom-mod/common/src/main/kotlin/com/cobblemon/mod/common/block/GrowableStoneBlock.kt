/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

/**
 * Used for blocks that grow similarly to Amethyst.
 *
 * @param settings Block settings
 * @param stage The stage of growth of the block
 * @param height The height of this block's hitbox
 * @param nextStage The next stage of this block's growth. Null if final stage
 *
 * @author whatsy
 */
abstract class GrowableStoneBlock(
    settings: Settings,
    val stage: Int,
    val height: Int,
    xzOffset: Int,
    val nextStage: Block?
) : FacingBlock(settings) {

    private val upShape: VoxelShape = Block.createCuboidShape(
        xzOffset.toDouble(),
        0.0,
        xzOffset.toDouble(),
        (16 - xzOffset).toDouble(),
        height.toDouble(),
        (16 - xzOffset).toDouble())

    private val downShape: VoxelShape = createCuboidShape(
        xzOffset.toDouble(),
        (16 - height).toDouble(),
        xzOffset.toDouble(),
        (16 - xzOffset).toDouble(),
        16.0,
        (16 - xzOffset).toDouble())

    private val northShape: VoxelShape = createCuboidShape(
        xzOffset.toDouble(),
        xzOffset.toDouble(),
        (16 - height).toDouble(),
        (16 - xzOffset).toDouble(),
        (16 - xzOffset).toDouble(),
        16.0)

    private val southShape: VoxelShape = createCuboidShape(
        xzOffset.toDouble(),
        xzOffset.toDouble(),
        0.0,
        (16 - xzOffset).toDouble(),
        (16 - xzOffset).toDouble(),
        height.toDouble())

    private val eastShape: VoxelShape = createCuboidShape(
        0.0,
        xzOffset.toDouble(),
        xzOffset.toDouble(),
        height.toDouble(),
        (16 - xzOffset).toDouble(),
        (16 - xzOffset).toDouble())

    private val westShape: VoxelShape = createCuboidShape(
        (16 - height).toDouble(),
        xzOffset.toDouble(),
        xzOffset.toDouble(),
        16.0,
        (16 - xzOffset).toDouble(),
        (16 - xzOffset).toDouble())

    init {
        this.defaultState = this.stateManager.defaultState
            .with(FACING, Direction.DOWN)
    }

    abstract fun canGrow(pos: BlockPos, world: BlockView): Boolean

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun hasRandomTicks(state: BlockState?) = stage < MAX_STAGE
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.random.nextInt(5) == 0 && canGrow(pos, world)) {
            val block = nextStage

            if (block != null) {
                val newState = block.defaultState.with(FACING, state.get(FACING)) as BlockState
                world.setBlockState(pos, newState)
            }
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        var blockState = defaultState
        val worldView = ctx.world
        val blockPos = ctx.blockPos
        blockState = blockState.with(FACING, ctx.side) as BlockState
        if (blockState.canPlaceAt(worldView, blockPos)) {
            return blockState
        }
        return null
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val direction = state.get(FACING) as Direction
        val blockState = world.getBlockState(pos.offset(direction.opposite))
        return blockState.isSideSolidFullSquare(world, pos, direction)
    }

    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState? {
        return if (direction == state.get(FACING).opposite && !state.canPlaceAt(world, pos)) Blocks.AIR.defaultState
        else super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val direction = state.get(FACING) as Direction
        return when (direction) {
            Direction.NORTH -> northShape
            Direction.SOUTH -> southShape
            Direction.EAST -> eastShape
            Direction.WEST -> westShape
            Direction.DOWN -> downShape
            Direction.UP -> upShape
            else -> upShape
        }
    }

    companion object {
        const val STAGE_0 = 0
        const val STAGE_1 = 1
        const val STAGE_2 = 2
        const val STAGE_3 = 3

        const val MAX_STAGE = STAGE_3
        const val MIN_STAGE = STAGE_0
    }
}