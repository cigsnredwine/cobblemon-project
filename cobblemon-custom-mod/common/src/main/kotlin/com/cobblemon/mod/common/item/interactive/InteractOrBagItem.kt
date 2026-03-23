/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.callback.MoveSelectCallbacks
import com.cobblemon.mod.common.api.callback.MoveSelectDTO
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.BagItemActionResponse
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.item.battle.BagItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult

// TODO probably remove this? Still need to work through Ether logic and apply in other places

interface InteractOrBagItem {
    fun canUseOverworld(pokemon: Pokemon): Boolean
    fun canUseBattle(battlePokemon: BattlePokemon): Boolean

    fun getBagItem(stack: ItemStack): BagItem?

    fun onRegularUse(world: ServerWorld, user: ServerPlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return TypedActionResult.success(user.getStackInHand(hand))
    }



    fun onBattleUse(player: ServerPlayerEntity, battlePokemon: BattlePokemon, stack: ItemStack): Boolean {
        val battle = battlePokemon.actor.battle

        val bagItem = getBagItem(stack) ?: return false
        if (!battlePokemon.actor.canFitForcedAction()) {
            player.sendMessage(battleLang("bagitem.cannot").red())
            return false
        }

        if (!bagItem.canUse(battle, battlePokemon)) {
            player.sendMessage(battleLang("bagitem.invalid").red())
            return false
        }

        val turn = battle.turn
        MoveSelectCallbacks.create(
            player = player,
            possibleMoves = battlePokemon.moveSet.map { move ->
                val enabled = move.currentPp < move.maxPp
                return@map MoveSelectDTO(move).also { it.enabled = enabled }
            }
        ) { _, _, move ->
            if (stack in player.handItems && !stack.isEmpty && battlePokemon.actor.canFitForcedAction() && battle.turn == turn) {
                battlePokemon.actor.forceChoose(BagItemActionResponse(bagItem, battlePokemon, move.moveTemplate.name))
                if (!player.isCreative) {
                    stack.decrement(1)
                }
            }
        }

        return true
    }

    fun checkBattleItem(player: ServerPlayerEntity, battle: PokemonBattle, actor: BattleActor, battlePokemon: BattlePokemon, stack: ItemStack, hand: Hand): Boolean {

        val bagItem = getBagItem(stack) ?: return false

        if (!actor.canFitForcedAction()) {
            player.sendMessage(battleLang("bagitem.cannot").red())
            return false
        }

        if (!bagItem.canUse(battle, battlePokemon)) {
            player.sendMessage(battleLang("bagitem.invalid").red())
            return false
        }

        return player.getStackInHand(hand) === stack
    }
}