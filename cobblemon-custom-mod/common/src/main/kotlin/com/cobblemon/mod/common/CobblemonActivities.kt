/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import net.minecraft.entity.ai.brain.Activity

object CobblemonActivities {
    val activities = mutableListOf<Activity>()
    val BATTLING_ACTIVITY = Activity("pokemon_battling")

    fun register(activity: Activity): Activity {
        activities.add(activity)
        return activity
    }
}