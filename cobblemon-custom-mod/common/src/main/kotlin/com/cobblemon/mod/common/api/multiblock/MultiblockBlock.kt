/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.multiblock

import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

/**
 * A block that can be part of a [MultiblockStructure]
 */
abstract class MultiblockBlock(properties: Settings) : BlockWithEntity(properties) {

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        if (world is ServerWorld) {
            val multiblockEntity = world.getBlockEntity(pos) as? MultiblockEntity
            multiblockEntity?.multiblockBuilder?.validate(world)
        }
        super.onPlaced(world, pos, state, placer, itemStack)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if(hand == Hand.OFF_HAND) {
            return ActionResult.SUCCESS
        }
        val entity = world.getBlockEntity(pos) as MultiblockEntity?
        if (entity?.multiblockStructure != null) {
            return entity.multiblockStructure!!.onUse(state, world, pos, player, hand, hit)
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        if (!world.isClient) {
            val entity = world.getBlockEntity(pos)
            if (entity is MultiblockEntity && entity.multiblockStructure != null) {
                entity.multiblockStructure!!.onBreak(world, pos, state, player)
            }
            entity?.markRemoved()
        }
        super.onBreak(world, pos, state, player)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return createMultiBlockEntity(pos, state)
    }

    @Deprecated("Deprecated in Java")
    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    //This is done so a block picked with NBT doesnt absolutely DESTROY multiblocks
    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState): ItemStack {
        val blockEntity = world.getBlockEntity(pos) as? MultiblockEntity ?: return ItemStack.EMPTY
        return if (blockEntity.multiblockStructure == null) super.getPickStack(world, pos, state) else ItemStack.EMPTY
    }

    abstract fun createMultiBlockEntity(pos: BlockPos, state: BlockState): FossilMultiblockEntity

}
