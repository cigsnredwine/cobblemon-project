/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.registry.Registries
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class RestorationTankBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.RESTORATION_TANK)  {
    val inv = RestorationTankInventory(this)

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.put("inventory", inv.toNbtList())
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains("Items")) {
            // Old format
            val items = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY)
            Inventories.readNbt(nbt, items)
            RestorationTankInventory(this, *items.toTypedArray())
        } else if (nbt.contains("inventory")) {
            // New format
            inv.readNbtList(nbt.get("inventory") as NbtList)
        }
    }

    class RestorationTankInventory(val tankEntity: RestorationTankBlockEntity, vararg items: ItemStack) : SimpleInventory(*items), SidedInventory {

        constructor(tankEntity: RestorationTankBlockEntity) : this(tankEntity, *Array(8) { ItemStack.EMPTY })

        override fun getMaxCountPerStack(): Int {
            return 1
        }

        override fun markDirty() {
            super.markDirty()
            for(i in 0 until size()) { // iterate over inventory, consume items, generate and place return items
                val itemStack : ItemStack = this.getStack(i)
                if(!itemStack.isEmpty) {
                    val struct = tankEntity.multiblockStructure as? FossilMultiblockStructure
                    val returnIdentifier = NaturalMaterials.getReturnItem(itemStack)
                    if(tankEntity.world != null) {
                        if(struct?.insertOrganicMaterial(itemStack, tankEntity.world!!) == true) {
                            removeStack(i)
                            if (returnIdentifier != null) {
                                val returnItem = Registries.ITEM.get(returnIdentifier)
                                val returnStack =  ItemStack(returnItem, itemStack.count)
                                var done = false
                                    for(j in 1 until size()) {
                                        // Look for a inventory slot to merge with
                                        val existingStack : ItemStack = getStack(j)
                                        if (existingStack.isEmpty
                                                || (Registries.ITEM.getId(existingStack.item) == returnIdentifier && existingStack.count + returnStack.count < existingStack.maxCount)) {
                                            setStack(j, ItemStack(returnItem, existingStack.count + returnStack.count))
                                            done = true
                                        }
                                    }
                                if (!done) {
                                    setStack(i, ItemStack(returnItem, itemStack.count))
                                }
                            }
                        }
                    }
                }
            }

            tankEntity.world?.let {
//                tankEntity.world!!.updateListeners(tankEntity.pos, tankEntity.cachedState, tankEntity.cachedState, Block.NOTIFY_ALL)
//                tankEntity.world!!.updateComparators(tankEntity.pos, tankEntity.world!!.getBlockState(tankEntity.pos).block)
                tankEntity.markDirty()
                tankEntity.multiblockStructure?.markDirty(it)
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return if (side == Direction.DOWN) intArrayOf(0,1,2,3,4,5,6,7) else intArrayOf(0)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (tankEntity.multiblockStructure is FossilMultiblockStructure) {
                if(dir != Direction.DOWN) {
                    val structure = tankEntity.multiblockStructure as FossilMultiblockStructure
                    val canUtilize = stack?.let { NaturalMaterials.isNaturalMaterial(it) } == true
                            && structure.organicMaterialInside < FossilMultiblockStructure.MATERIAL_TO_START
                            && !structure.hasCreatedPokemon
                    val returnItem = NaturalMaterials.getReturnItem(stack!!) ?: return canUtilize
                    val returnStack = ItemStack(Registries.ITEM.get(returnItem), stack.count)
                    if(canUtilize && super.canInsert(returnStack)) {
                        if(returnStack == ItemStack.EMPTY && getStack(0) == ItemStack.EMPTY) {
                            return true
                        }
                        // See if there's room for the return item
                        var emptyCount = 0
                        for(i in 1 until size()) {
                            val existingStack : ItemStack = getStack(i)
                            if(existingStack == ItemStack.EMPTY) {
                                emptyCount += 1
                                if(emptyCount > 1) {
                                    // 2 empty slots, safe to insert
                                    return true
                                }
                            }
                        }
                    }
                }
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return dir == Direction.DOWN && slot >= 0 && slot < size()
        }

    }
}