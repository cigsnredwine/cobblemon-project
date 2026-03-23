/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.GildedChestBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BlockEntityModelRepository
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.Properties
import net.minecraft.util.math.RotationAxis

class GildedChestBlockRenderer(context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<GildedChestBlockEntity> {
    override fun render(
        entity: GildedChestBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val aspects = emptySet<String>()
        val state = entity.poseableState
        state.updatePartialTicks(tickDelta)

        val poserId = entity.type.poserId

        val model = BlockEntityModelRepository.getPoser(poserId, aspects)
        val texture = BlockEntityModelRepository.getTexture(poserId, aspects, state.animationSeconds)
        val vertexConsumer = vertexConsumers.getBuffer(model.getLayer(texture))
        model.bufferProvider = vertexConsumers
        state.currentModel = model

        matrices.push()
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
        matrices.translate(-0.5, 0.0, 0.5)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.cachedState.get(Properties.HORIZONTAL_FACING).asRotation()))
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f))

        model.setupAnimStateful(
            entity = null,
            state = state,
            headYaw = 0F,
            headPitch = 0F,
            limbSwing = 0F,
            limbSwingAmount = 0F,
            ageInTicks = state.animationSeconds * 20
        )
        model.render(matrices, vertexConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f)
        model.withLayerContext(vertexConsumers, state, BlockEntityModelRepository.getLayers(poserId, aspects)) {
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
        }
        model.setDefault()
        matrices.pop()

    }
}