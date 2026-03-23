/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mechanics

import com.bedrockk.molang.Expression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.util.asExpression
import com.cobblemon.mod.common.util.resolveInt

class RemediesMechanic {
    val healingAmounts = mutableMapOf<String, Expression>()
    val friendshipDrop = "10".asExpression()

    fun getHealingAmount(type: String, runtime: MoLangRuntime, default: Int = 20) = healingAmounts[type]?.let { runtime.resolveInt(it) } ?: default
    fun getFriendshipDrop(runtime: MoLangRuntime) = runtime.resolveInt(friendshipDrop)
}