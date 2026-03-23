/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pasture

import com.cobblemon.mod.common.Cobblemon
import java.util.UUID
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

class PastureLink(val linkId: UUID, val pcId: UUID, val dimension: Identifier, val pos: BlockPos, val permissions: PasturePermissions) {
    fun getPC() = Cobblemon.storage.getPC(pcId)
}