/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.FossilAnalyzerBlockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d

class FossilAnalyzerRenderer(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<FossilAnalyzerBlockEntity> {

    override fun render(
        entity: FossilAnalyzerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int
    ) {
        val blockState = if (entity.world != null) entity.cachedState
            else (CobblemonBlocks.FOSSIL_ANALYZER.defaultState.with(HorizontalFacingBlock.FACING, Direction.SOUTH) as BlockState)
        // We shouldn't have to do any complex rendering when the block isn't a multiblock
        if (entity.multiblockStructure == null) {
            return
        }
        val direction = blockState.get(HorizontalFacingBlock.FACING)
        val yRot = direction.asRotation() + if(direction == Direction.WEST || direction == Direction.EAST) 180F else 0F
        val struct = entity.multiblockStructure as FossilMultiblockStructure

        struct.fossilInventory.forEachIndexed { index, fossilStack ->
            matrices.push()
            
            val dirOffset = when (direction) {
                Direction.NORTH -> Vec3d(0.0, 0.0, 0.05)
                Direction.SOUTH -> Vec3d(0.0, 0.0, -0.05)
                Direction.EAST -> Vec3d(-0.05, 0.0, 0.0)
                Direction.WEST -> Vec3d(0.05, 0.0, 0.0)
                else -> Vec3d.ZERO
            }
            matrices.translate(0.5 + dirOffset.x,0.4 + (index * 0.1) + dirOffset.y, 0.5 + dirOffset.z)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot))
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180F))
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90F))
            matrices.scale(0.7F, 0.7F, 0.7F)

            MinecraftClient.getInstance().itemRenderer.renderItem(fossilStack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, entity.world, 0)

            matrices.pop()
        }
    }
}