/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mechanics

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.util.asExpression

class BerriesMechanic {
    val portionHealRatio: Expression = "0.33".asExpression()
    val sitrusHealAmount: Expression = "v.pokemon.max_hp * 0.33".asExpression()
    val friendshipRaiseAmount: Expression = "v.pokemon.friendship < 100 ? 10 : (v.pokemon.friendship < 200 ? 5 : 1)".asExpression()
    val evLowerAmount: Expression = "10".asExpression()
    val ppRestoreAmount: Expression = "10".asExpression()
    val oranRestoreAmount: Expression = "10".asExpression()
}