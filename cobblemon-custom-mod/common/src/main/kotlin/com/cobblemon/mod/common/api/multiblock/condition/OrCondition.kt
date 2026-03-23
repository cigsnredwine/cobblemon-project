/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.multiblock.condition

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShape

class OrCondition(
    val conditionOne: MultiblockCondition,
    val conditionTwo: MultiblockCondition
) : MultiblockCondition {
    override fun test(world: ServerWorld, box: VoxelShape): Boolean {
        return conditionOne.test(world, box) or conditionTwo.test(world, box)
    }

}
