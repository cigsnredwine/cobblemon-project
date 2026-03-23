/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.multiblock

import com.cobblemon.mod.common.api.multiblock.MultiblockStructure
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos

/**
 * Multiblock entities are kind of complicated. Basically every multiblock entity should have a MultiBlockStructureBuilder
 * that is checked when this block entity is created. The multiblock entity also contains a reference to a MultiblockStructure
 * which is shared between all MultiblockEntities in the structure. Finally, every Multiblock contains the location of the
 * block that controls this blockentities associated structure
 */
abstract class MultiblockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    var multiblockBuilder: MultiblockStructureBuilder?
) : BlockEntity(type, pos, state) {

    abstract var multiblockStructure: MultiblockStructure?
    abstract var masterBlockPos: BlockPos?

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        val result = NbtCompound()
        writeNbt(result)
        return result
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        //Used for checking build conditions in multiblocks (Dont count a block if it has the FORMED flag)
        nbt.putBoolean(DataKeys.FORMED, masterBlockPos != null)
        if (multiblockStructure != null && multiblockStructure!!.controllerBlockPos == pos) {
            nbt.put(DataKeys.MULTIBLOCK_STORAGE, multiblockStructure!!.writeToNbt())
        }
        else if (masterBlockPos != null) {
            nbt.put(DataKeys.CONTROLLER_BLOCK, NbtHelper.fromBlockPos(masterBlockPos))
        }
    }

    abstract override fun readNbt(nbt: NbtCompound)

}
