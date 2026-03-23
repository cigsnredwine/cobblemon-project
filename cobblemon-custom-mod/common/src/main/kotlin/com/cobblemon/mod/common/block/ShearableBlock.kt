/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Represents a block that can be sheared.
 * This is used to compliment our Mixin implementation for Dispenser compatibility.
 */
interface ShearableBlock {

    /**
     * Attempts to shear this block.
     *
     * @param world The [World] where the shear is being attempted.
     * @param state The current [BlockState] of this block.
     * @param pos The [BlockPos] of this block.
     * @param successCallback The callback invoked if the shear attempt was successful.
     * @return If the shearing was successful.
     */
    fun attemptShear(world: World, state: BlockState, pos: BlockPos, successCallback: () -> Unit = {}): Boolean

}