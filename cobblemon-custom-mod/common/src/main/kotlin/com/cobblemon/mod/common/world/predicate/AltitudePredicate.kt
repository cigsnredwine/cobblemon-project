/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.predicate

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.Optional
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.blockpredicate.BlockPredicate

class AltitudePredicate(val min: Optional<Int>, val max: Optional<Int>) : BlockPredicate {
    val range = min.orElse(Int.MIN_VALUE)..max.orElse(Int.MAX_VALUE)

    override fun test(world: StructureWorldAccess, block: BlockPos) = block.y in range
    override fun getType() = CobblemonBlockPredicates.ALTITUDE

    companion object {
        val CODEC : Codec<AltitudePredicate> = RecordCodecBuilder.create {
            it.group(
                PrimitiveCodec.INT.optionalFieldOf("min").forGetter(AltitudePredicate::min),
                PrimitiveCodec.INT.optionalFieldOf("max").forGetter(AltitudePredicate::max)
            ).apply(it, ::AltitudePredicate)
        }
    }
}