/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.mulch

import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

/**
 * Represents something that can have mulch applied to it.
 * Typically, this will be a block implementation.
 */
interface Mulchable {

    /**
     * Checks if mulch can be applied to a specific spot in the world.
     *
     * @param world The affected [ServerWorld].
     * @param pos The [BlockPos] being checked.
     * @param state The [BlockState] of the block at the [pos] in the [world].
     * @param variant The [MulchVariant] being applied.
     * @return If the application is possible.
     */
    fun canHaveMulchApplied(world: ServerWorld, pos: BlockPos, state: BlockState, variant: MulchVariant): Boolean

    /**
     * Applies the mulch to the specified spot.
     * This is expected to be invoked if [canHaveMulchApplied] is true.
     *
     * @param world The affected [ServerWorld].
     * @param random The [Random] instance being used in the interaction.
     * @param pos The [BlockPos] being checked.
     * @param state The [BlockState] of the block at the [pos] in the [world].
     * @param variant The [MulchVariant] being applied.
     */
    fun applyMulch(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState, variant: MulchVariant)

}