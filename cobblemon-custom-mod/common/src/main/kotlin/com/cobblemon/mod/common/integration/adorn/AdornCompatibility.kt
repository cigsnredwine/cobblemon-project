/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.integration.adorn

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.ResourcePackActivationBehaviour
import com.cobblemon.mod.common.mixin.invoker.AdornRegisterInvoker
import com.cobblemon.mod.common.util.cobblemonResource
import juuxel.adorn.block.variant.BlockKind
import juuxel.adorn.block.variant.BlockVariant
import juuxel.adorn.block.variant.BlockVariantSet
import juuxel.adorn.block.variant.BlockVariantSets
import net.minecraft.text.Text
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
object AdornCompatibility : BlockVariantSet {
    override val woodVariants: List<BlockVariant> = listOf(
        BlockVariant.Wood("${Cobblemon.MODID}/apricorn")
    )
}