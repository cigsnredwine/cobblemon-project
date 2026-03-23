/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.multiblock.MultiblockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.api.multiblock.MultiblockStructure
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos

open class FossilMultiblockEntity(
    pos: BlockPos,
    state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder,
    type: BlockEntityType<*> = CobblemonBlockEntities.FOSSIL_MULTIBLOCK
) : MultiblockEntity(type, pos, state, multiblockBuilder) {

    override var masterBlockPos: BlockPos? = null

    override var multiblockStructure: MultiblockStructure? = null
        set(structure) {
            field = structure
            if (structure != null) {
                masterBlockPos = structure.controllerBlockPos
            }
        }
        get() {
            if(masterBlockPos != null && masterBlockPos != pos) {
                val chunkPos = ChunkPos(masterBlockPos)
                if (world?.chunkManager?.isChunkLoaded(chunkPos.x, chunkPos.z) == true) {
                    val entity: FossilMultiblockEntity? = world?.getBlockEntity(masterBlockPos) as FossilMultiblockEntity?
                    field = entity?.multiblockStructure
                }
            }
            return field
        }

    override fun markRemoved() {
        super.markRemoved()
        if(this.multiblockStructure != null && world != null) {
            this.multiblockStructure!!.markRemoved(world!!)
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        val oldMultiblockStructure = this.multiblockStructure as? FossilMultiblockStructure
        multiblockStructure = if (nbt.contains(DataKeys.MULTIBLOCK_STORAGE)) {
            if(oldMultiblockStructure?.fossilState != null) {
                // Copy the fossilState's previous animation time to the new instance
                // Otherwise the fetus animation gets interrupted on every block update
                val animAge = oldMultiblockStructure.fossilState.peekAge() // If someone knows a better way to fetch the age, please do.
                val partialTicks = oldMultiblockStructure.fossilState.getPartialTicks()
                FossilMultiblockStructure.fromNbt(nbt.getCompound(DataKeys.MULTIBLOCK_STORAGE), animAge, partialTicks )
            } else {
                FossilMultiblockStructure.fromNbt(nbt.getCompound(DataKeys.MULTIBLOCK_STORAGE))
            }
        } else {
            null
        }
        masterBlockPos = if (nbt.contains(DataKeys.CONTROLLER_BLOCK)) {
            NbtHelper.toBlockPos(nbt.getCompound(DataKeys.CONTROLLER_BLOCK))
        } else {
            null
        }
    }

}