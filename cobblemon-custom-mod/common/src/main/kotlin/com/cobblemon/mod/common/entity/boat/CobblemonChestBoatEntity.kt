/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.boat

import com.cobblemon.mod.common.CobblemonEntities
import net.minecraft.entity.EntityType
import net.minecraft.entity.RideableInventory
import net.minecraft.entity.mob.PiglinBrain
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.vehicle.VehicleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

@Suppress("unused")
class CobblemonChestBoatEntity(entityType: EntityType<CobblemonChestBoatEntity>, world: World) : CobblemonBoatEntity(entityType, world), RideableInventory, VehicleInventory {

    constructor(world: World) : this(CobblemonEntities.CHEST_BOAT, world)

    // This exists cause super passes in vanilla boat entity type
    constructor(world: World, x: Double, y: Double, z: Double) : this(CobblemonEntities.CHEST_BOAT, world) {
        this.setPosition(x, y, z)
        this.prevX = x
        this.prevY = y
        this.prevZ = z
    }

    private var inventory = this.emptyInventory()
    private var lootTableId: Identifier? = null
    private var lootTableSeed = 0L

    override fun openInventory(player: PlayerEntity) {
        player.openHandledScreen(this)
        if (!player.world.isClient) {
            this.emitGameEvent(GameEvent.CONTAINER_OPEN, player)
            PiglinBrain.onGuardedBlockInteracted(player, true)
        }
    }

    override fun clear() = this.clearInventory()

    override fun size(): Int = INVENTORY_SLOTS

    override fun getStack(slot: Int): ItemStack = this.getInventoryStack(slot)

    override fun removeStack(slot: Int, amount: Int): ItemStack = this.removeInventoryStack(slot, amount)

    override fun removeStack(slot: Int): ItemStack = this.removeInventoryStack(slot)

    override fun setStack(slot: Int, stack: ItemStack) = this.setInventoryStack(slot, stack)

    override fun markDirty() {}

    override fun canPlayerUse(player: PlayerEntity): Boolean = this.canPlayerAccess(player)

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        if (this.lootTableId != null && player.isSpectator) {
            return null
        }
        this.generateInventoryLoot(playerInventory.player)
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this)
    }

    override fun getLootTableId(): Identifier? = this.lootTableId

    override fun setLootTableId(lootTableId: Identifier?) {
        this.lootTableId = lootTableId
    }

    override fun getLootTableSeed(): Long = this.lootTableSeed

    override fun setLootTableSeed(lootTableSeed: Long) {
        this.lootTableSeed = lootTableSeed
    }

    override fun getInventory(): DefaultedList<ItemStack> = this.inventory

    override fun resetInventory() {
        this.inventory = this.emptyInventory()
    }

    override fun asItem(): Item = this.boatType.chestBoatItem

    private fun emptyInventory(): DefaultedList<ItemStack> = DefaultedList.ofSize(INVENTORY_SLOTS, ItemStack.EMPTY)

    companion object {

        private const val INVENTORY_SLOTS = 27

    }

}