/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.multiblock

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

interface MultiblockStructure {
    val controllerBlockPos: BlockPos

    fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult

    fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?)

    fun tick(world: World)

    fun syncToClient(world: World)

    fun markDirty(world: World)
    fun writeToNbt(): NbtCompound
    fun getComparatorOutput(state: BlockState, world: World?, pos: BlockPos?): Int {
        return 0
    }

    fun markRemoved(world: World):Unit
    fun onTriggerEvent(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?)
}