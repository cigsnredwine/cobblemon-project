/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.platform.events

import com.cobblemon.mod.common.api.events.Cancelable
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

/**
 * Events related to a [ServerPlayerEntity].
 * As implied by the name these are fired on the server side.
 *
 * @author Licious
 * @since February 15th, 2023
 */
interface ServerPlayerEvent {

    /**
     * The [ServerPlayerEntity] triggering the platform specific events.
     */
    val player: ServerPlayerEntity

    /**
     * Fired when the [player] logs in.
     */
    data class Login(override val player: ServerPlayerEntity) : ServerPlayerEvent

    /**
     * Fired when the [player] logs out.
     */
    data class Logout(override val player: ServerPlayerEntity) : ServerPlayerEvent

    /**
     * Fired when the [player] dies.
     * If canceled the death will be prevented but healing is required in order to not be stuck in a loop.
     */
    data class Death(override val player: ServerPlayerEntity) : ServerPlayerEvent, Cancelable()

    /**
     * Fired when the [player] right clicks a block.
     * When canceled no interaction will occur.
     *
     * @property pos The [BlockPos] of the targeted block.
     * @property hand The [Hand] that hit the block.
     * @property face The [Direction] of the block if any.
     */
    data class RightClickBlock(override val player: ServerPlayerEntity, val pos: BlockPos, val hand: Hand, val face: Direction?) : ServerPlayerEvent, Cancelable()

    /**
     * Fired when the [player] right clicks an entity.
     * When canceled no interaction will occur.
     *
     * @property item The [ItemStack] clicked on the [entity].
     * @property hand The [Hand] that clicked the [entity].
     * @property entity The [Entity] the [player] clicked.
     */
    data class RightClickEntity(override val player: ServerPlayerEntity, val item: ItemStack, val hand: Hand, val entity: Entity): ServerPlayerEvent, Cancelable()
}