/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.net.messages.client.battle.BattleFaintPacket
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |faint|POKEMON
 *
 * POKEMON has fainted.
 * @author Deltric
 * @since January 22nd, 2022
 */
class FaintInstruction(battle: PokemonBattle, val message: BattleMessage) : InterpreterInstruction {
    var waitTime = 2.5F
    val faintingPokemon = message.battlePokemon(0, battle)!!

    override fun invoke(battle: PokemonBattle) {

        battle.dispatchWaiting(waitTime) {
            val (pnx, _) = message.pnxAndUuid(0) ?: return@dispatchWaiting
            battle.sendUpdate(BattleFaintPacket(pnx))
            faintingPokemon.effectedPokemon.currentHealth = 0
            faintingPokemon.sendUpdate()
            val context = ShowdownInterpreter.getContextFromFaint(faintingPokemon, battle)
            CobblemonEvents.BATTLE_FAINTED.post(BattleFaintedEvent(battle, faintingPokemon, context))

            battle.getActorAndActiveSlotFromPNX(pnx).second.battlePokemon = null
            faintingPokemon.contextManager.add(context)
            faintingPokemon.contextManager.clear(BattleContext.Type.STATUS, BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
            battle.majorBattleActions[faintingPokemon.uuid] = message
        }
        battle.dispatchWaiting(0.5F) {
            val faintMessage = battleLang("fainted", faintingPokemon.getName()).red()
            battle.broadcastChatMessage(faintMessage)
        }
    }
}