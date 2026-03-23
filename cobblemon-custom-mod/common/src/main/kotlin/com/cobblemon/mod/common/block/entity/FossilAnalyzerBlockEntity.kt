/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.Fossils
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class FossilAnalyzerBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
): FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.FOSSIL_ANALYZER) {

    val inv = FossilAnalyzerInventory(this)

    class FossilAnalyzerInventory(val analyzerEntity: FossilAnalyzerBlockEntity) : SidedInventory {
        override fun clear() {
            if(analyzerEntity.multiblockStructure != null && analyzerEntity.multiblockStructure is FossilMultiblockStructure) {
                val fossilMultiblockStructure = analyzerEntity.multiblockStructure as FossilMultiblockStructure
                fossilMultiblockStructure.fossilInventory.clear()
            }
        }

        override fun size(): Int {
            if (analyzerEntity.multiblockStructure != null) {
                return 3
            }
            return 0
        }

        override fun isEmpty(): Boolean {
            return false
        }

        override fun getStack(slot: Int): ItemStack {
            if (analyzerEntity.multiblockStructure != null && analyzerEntity.multiblockStructure is FossilMultiblockStructure) {
                val fossilMultiblockStructure = analyzerEntity.multiblockStructure as FossilMultiblockStructure
                if (fossilMultiblockStructure.fossilInventory.size > slot) {
                    return fossilMultiblockStructure.fossilInventory[slot]
                }
            }
            return ItemStack.EMPTY
        }

        //Unimplemented since no extract
        override fun removeStack(slot: Int, amount: Int): ItemStack {
            return removeStack(slot)
        }

        override fun removeStack(slot: Int): ItemStack {
            return ItemStack.EMPTY
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            if (analyzerEntity.multiblockStructure != null && analyzerEntity.multiblockStructure is FossilMultiblockStructure) {
                val struct = analyzerEntity.multiblockStructure as FossilMultiblockStructure
                analyzerEntity.world?.let {
                    struct.insertFossil(stack, it)
                }
            }
        }

        override fun markDirty() {
            if (analyzerEntity.world != null) {
                analyzerEntity.multiblockStructure?.markDirty(analyzerEntity.world!!)
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            if(analyzerEntity.multiblockStructure != null && analyzerEntity.multiblockStructure is FossilMultiblockStructure) {
                return intArrayOf(0,1,2)
            }
            return IntArray(0)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (analyzerEntity.multiblockStructure is FossilMultiblockStructure) {
                val structure = analyzerEntity.multiblockStructure as FossilMultiblockStructure
                return stack?.let { Fossils.isFossilIngredient(it) } == true
                        && structure.fossilInventory.size < Cobblemon.config.maxInsertedFossilItems
                        && structure.isRunning() == false && structure.resultingFossil == null
                        && Fossils.getSubFossilByItemStacks( structure.fossilInventory + mutableListOf(stack) ) != null
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return false
        }
    }
}