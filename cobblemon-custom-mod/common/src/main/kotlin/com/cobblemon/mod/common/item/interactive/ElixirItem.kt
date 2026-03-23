/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.item.PokemonSelectingItem
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.battle.BagItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.giveOrDropItemStack
import kotlin.math.min
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

/**
 * Items for recovering PP in all moves at once.
 *
 * @author Hiroku
 * @since June 30th, 2023
 */
class ElixirItem(val max: Boolean) : CobblemonItem(Settings()), PokemonSelectingItem {
    override val bagItem = object : BagItem {
        override val itemName = "item.cobblemon.${ if (max) "max_elixir" else "elixir" }"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) = target.health > 0 && target.moveSet.any { it.currentPp < it.maxPp }
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "elixir".let { if (!max) "$it 10" else it }
    }

    override fun canUseOnPokemon(pokemon: Pokemon) = pokemon.moveSet.any { it.currentPp < it.maxPp }
    override fun applyToPokemon(player: ServerPlayerEntity, stack: ItemStack, pokemon: Pokemon): TypedActionResult<ItemStack> {
        var changed = false
        pokemon.moveSet.doWithoutEmitting {
            pokemon.moveSet.getMoves().forEach {
                if (it.currentPp < it.maxPp) {
                    if (max) {
                        it.currentPp = it.maxPp
                    } else {
                        it.currentPp = min(it.currentPp + 10, it.maxPp)
                    }
                    changed = true
                }
            }
        }

        return if (changed) {
            pokemon.moveSet.update()
            if (!player.isCreative) {
                stack.decrement(1)
                player.giveOrDropItemStack(ItemStack(Items.GLASS_BOTTLE))
            }
            player.playSound(CobblemonSounds.MEDICINE_LIQUID_USE, SoundCategory.PLAYERS, 1F, 1F)
            TypedActionResult.success(stack)
        } else {
            TypedActionResult.fail(stack)
        }
    }

    override fun applyToBattlePokemon(player: ServerPlayerEntity, stack: ItemStack, battlePokemon: BattlePokemon) {
        super.applyToBattlePokemon(player, stack, battlePokemon)
        player.playSound(CobblemonSounds.MEDICINE_LIQUID_USE, SoundCategory.PLAYERS, 1F, 1F)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world is ServerWorld && user is ServerPlayerEntity) {
            val stack = user.getStackInHand(hand)
            return use(user, stack)
        }
        return TypedActionResult.success(user.getStackInHand(hand))
    }
}