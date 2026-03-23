/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client

import com.cobblemon.mod.common.client.battle.ClientBattleChallenge
import com.cobblemon.mod.common.client.trade.ClientTradeOffer

class ClientPlayerActionRequests {
    val battleChallenges = mutableListOf<ClientBattleChallenge>()
    val tradeOffers = mutableListOf<ClientTradeOffer>()
}