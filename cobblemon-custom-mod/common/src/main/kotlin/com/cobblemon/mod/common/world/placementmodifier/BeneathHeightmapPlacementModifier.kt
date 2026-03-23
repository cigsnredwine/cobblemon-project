/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.placementmodifier

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.Optional
import java.util.stream.Stream
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.feature.FeaturePlacementContext
import net.minecraft.world.gen.placementmodifier.PlacementModifier

/**
 * A placement modifier that returns a stream of positions underneath the given heightmap. It will start at some
 * offset and reach a specified number of blocks beneath.
 *
 * @author Hiroku
 * @since June 4th, 2023
 */
class BeneathHeightmapPlacementModifier(val heightmap: Heightmap.Type, val offset: Int, val reach: Int?) : PlacementModifier() {
    companion object {
        val MODIFIER_CODEC: Codec<BeneathHeightmapPlacementModifier> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    Heightmap.Type.CODEC.fieldOf("heightmap").forGetter { it.heightmap },
                    PrimitiveCodec.INT.fieldOf("offset").forGetter { it.offset },
                    PrimitiveCodec.INT.optionalFieldOf("reach").forGetter { Optional.ofNullable(it.reach) }
                )
                .apply(instance) { heightmap, offset, reach ->
                    BeneathHeightmapPlacementModifier(
                        heightmap,
                        offset,
                        reach.orElse(null)
                    )
                }
        }
    }

    override fun getType() = CobblemonPlacementModifierTypes.BENEATH_HEIGHTMAP
    override fun getPositions(context: FeaturePlacementContext, random: Random, pos: BlockPos): Stream<BlockPos> {
        var z: Int
        val x = pos.x
        val topY = context.getTopY(heightmap, x, pos.z.also { z = it }) + offset
        val positions = mutableListOf<BlockPos>()

        var y = topY
        while (y > context.bottomY && (reach == null || topY - y < reach)) {
            positions.add(BlockPos(x, y, z))
            y--
        }

        return positions.stream()
    }
}