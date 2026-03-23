/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.entity.boat.CobblemonBoatEntity
import com.cobblemon.mod.common.entity.boat.CobblemonBoatType
import com.cobblemon.mod.common.entity.boat.CobblemonChestBoatEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class CobblemonBoatItem(val boatType: CobblemonBoatType, val hasChest: Boolean, settings: Settings) : CobblemonItem(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val hitResult = raycast(world, user, FluidHandling.ANY)
        if (hitResult.type == HitResult.Type.MISS) {
            return TypedActionResult.pass(stack)
        }
        val vec3d = user.getRotationVec(1F)
        val eyePos = user.eyePos
        world.getOtherEntities(user, user.boundingBox.stretch(vec3d.multiply(5.0)).expand(1.0), RIDERS).forEach { entity ->
            val box = entity.boundingBox.expand(entity.targetingMargin.toDouble())
            if (box.contains(eyePos)) {
                return TypedActionResult.pass(stack)
            }
        }
        if (hitResult.type != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(stack)
        }
        val boatEntity = this.createBoat(world, hitResult)
        boatEntity.boatType = this.boatType
        boatEntity.yaw = user.yaw
        if (!world.isSpaceEmpty(boatEntity, boatEntity.boundingBox)) {
            return TypedActionResult.fail(stack)
        }
        if (!world.isClient) {
            world.spawnEntity(boatEntity)
            world.emitGameEvent(user, GameEvent.ENTITY_PLACE, hitResult.pos)
            if (!user.abilities.creativeMode) {
                stack.decrement(1)
            }
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this))
        return TypedActionResult.success(stack, world.isClient)
    }

    private fun createBoat(world: World, hitResult: HitResult): CobblemonBoatEntity {
        if (this.hasChest) {
            return CobblemonChestBoatEntity(world, hitResult.pos.x, hitResult.pos.y, hitResult.pos.z)
        }
        return CobblemonBoatEntity(world, hitResult.pos.x, hitResult.pos.y, hitResult.pos.z)
    }

    companion object {

        private val RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::canHit)

    }

}