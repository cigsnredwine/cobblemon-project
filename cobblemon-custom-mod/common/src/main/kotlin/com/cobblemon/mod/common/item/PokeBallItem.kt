/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.util.isServerSide
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.math.cos

class PokeBallItem(
    val pokeBall : PokeBall
) : CobblemonItem(Settings()) {

    override fun use(world: World, player: PlayerEntity, usedHand: Hand): TypedActionResult<ItemStack> {
        val itemStack = player.getStackInHand(usedHand)
        if (world.isServerSide()) {
            throwPokeBall(world, player as ServerPlayerEntity)
        }
        if (!player.abilities.creativeMode) {
            itemStack.decrement(1)
        }
        return TypedActionResult.success(itemStack, world.isClient)
    }

    private fun throwPokeBall(world: World, player: ServerPlayerEntity) {
        val pokeBallEntity = EmptyPokeBallEntity(pokeBall, player.world, player).apply {
            val overhandFactor: Float = if (player.pitch < 0) {
                5f * cos(player.pitch.toRadians())
            } else {
                5f
            }
            setVelocity(player, player.pitch - overhandFactor, player.yaw, 0.0f, pokeBall.throwPower, 1.0f)
            setPosition(pos.add(velocity.normalize().multiply(1.0)))
            owner = player
        }
        world.spawnEntity(pokeBallEntity)
    }

    override fun isFireproof(): Boolean {
        return pokeBall.name == PokeBalls.MASTER_BALL.name || super.isFireproof()
    }

}