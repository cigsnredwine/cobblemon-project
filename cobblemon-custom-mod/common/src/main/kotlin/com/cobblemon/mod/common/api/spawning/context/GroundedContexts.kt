/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.context

import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.WorldSlice
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import net.minecraft.block.BlockState
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

/**
 * A type of area based spawning context with a floor.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class FlooredSpawningContext(
    cause: SpawnCause,
    world: ServerWorld,
    position: BlockPos,
    light: Int,
    skyLight: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    height: Int,
    nearbyBlocks: List<BlockState>,
    slice: WorldSlice
) : AreaSpawningContext(cause, world, position, light, skyLight, canSeeSky, influences, height, nearbyBlocks, slice) {
    /** The block that the spawning is occurring on. */
    val baseBlock = slice.getBlockState(position.x, position.y, position.z)
}

/**
 * A land spawning context.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class GroundedSpawningContext(
    cause: SpawnCause,
    world: ServerWorld,
    position: BlockPos,
    light: Int,
    skyLight: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    height: Int,
    nearbyBlocks: List<BlockState>,
    slice: WorldSlice
) : FlooredSpawningContext(cause, world, position, light, skyLight, canSeeSky, influences, height, nearbyBlocks, slice)

/**
 * A spawning context that occurs at the bottom of a body of water.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class SeafloorSpawningContext(
    cause: SpawnCause,
    world: ServerWorld,
    position: BlockPos,
    light: Int,
    skyLight: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    height: Int,
    nearbyBlocks: List<BlockState>,
    slice: WorldSlice
) : FlooredSpawningContext(cause, world, position, light, skyLight, canSeeSky, influences, height, nearbyBlocks, slice) {
    override fun isSafeSpace(world: ServerWorld, pos: BlockPos, state: BlockState) = state.fluidState.isIn(FluidTags.WATER)
}

/**
 * A spawning context that occurs at the bottom of bodies of lava.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class LavafloorSpawningContext(
    cause: SpawnCause,
    world: ServerWorld,
    position: BlockPos,
    light: Int,
    skyLight: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    height: Int,
    nearbyBlocks: List<BlockState>,
    slice: WorldSlice
) : FlooredSpawningContext(cause, world, position, light, skyLight, canSeeSky, influences, height, nearbyBlocks, slice) {
    override fun isSafeSpace(world: ServerWorld, pos: BlockPos, state: BlockState) = state.fluidState.isIn(FluidTags.LAVA)
}

open class SurfaceSpawningContext(
    cause: SpawnCause,
    world: ServerWorld,
    position: BlockPos,
    light: Int,
    skyLight: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    height: Int,
    val depth: Int,
    nearbyBlocks: List<BlockState>,
    slice: WorldSlice
) : FlooredSpawningContext(cause, world, position, light, skyLight, canSeeSky, influences, height, nearbyBlocks, slice)