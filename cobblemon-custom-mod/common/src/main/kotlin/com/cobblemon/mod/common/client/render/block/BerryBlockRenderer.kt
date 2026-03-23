/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.block.entity.BerryBlockEntity
import com.cobblemon.mod.common.client.render.block.BerryBlockEntityRenderState
import com.cobblemon.mod.common.client.render.atlas.CobblemonAtlases
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.setPosition
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.cobblemon.mod.common.util.toVec3d
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f


class BerryBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BerryBlockEntity> {

    override fun isInRenderDistance(blockEntity: BerryBlockEntity, pos: Vec3d): Boolean {
        return super.isInRenderDistance(blockEntity, pos)
                && MinecraftClient.getInstance().worldRenderer.frustum.isVisible(Box.of(pos, 2.0, 4.0, 2.0))
    }

    override fun render(entity: BerryBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (!isInRenderDistance(entity, entity.pos.toVec3d())) return
        val blockState = entity.cachedState
        val age = blockState.get(BerryBlock.AGE)
        if (age <= BerryBlock.MATURE_AGE) {
            return
        }
        if (entity.renderState == null) {
            entity.renderState = BerryBlockEntityRenderState()
        }
        val renderState = entity.renderState as BerryBlockEntityRenderState
        if (renderState.needsRebuild || renderState.vboLightLevel != light) {
            renderToBuffer(entity, light, overlay, renderState.vbo)
            renderState.vboLightLevel = light
            (entity.renderState as BerryBlockEntityRenderState).needsRebuild = false
        }
        matrices.push()
        CobblemonRenderLayers.BERRY_LAYER.startDrawing()
        renderState.vbo.bind()
        renderState.vbo.draw(
            matrices.peek().positionMatrix.mul(RenderSystem.getModelViewMatrix()),
            RenderSystem.getProjectionMatrix(),
            GameRenderer.getRenderTypeCutoutProgram()
        )
        VertexBuffer.unbind()
        CobblemonRenderLayers.BERRY_LAYER.endDrawing()
        matrices.pop()
    }

    fun renderToBuffer(entity: BerryBlockEntity, light: Int, overlay: Int, buffer: VertexBuffer) {
        val blockState = entity.cachedState
        val age = blockState.get(BerryBlock.AGE)
        if (age <= BerryBlock.MATURE_AGE) {
            return
        }
        val isFlower = age == BerryBlock.FLOWER_AGE
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(CobblemonRenderLayers.BERRY_LAYER.drawMode, CobblemonRenderLayers.BERRY_LAYER.vertexFormat)
        for ((berry, growthPoint) in entity.berryAndGrowthPoint()) {
            val model = (if (isFlower) BerryModelRepository.modelOf(berry.flowerModelIdentifier) else BerryModelRepository.modelOf(berry.fruitModelIdentifier)) ?: continue
            model.setAngles(
                Math.toRadians(180.0 - growthPoint.rotation.x).toFloat(),
                Math.toRadians(180.0 + growthPoint.rotation.y).toFloat(),
                Math.toRadians(growthPoint.rotation.z).toFloat()
            )
            model.setPosition(Axis.X_AXIS.ordinal, growthPoint.position.x.toFloat())
            model.setPosition(Axis.Y_AXIS.ordinal, growthPoint.position.y.toFloat())
            model.setPosition(Axis.Z_AXIS.ordinal, growthPoint.position.z.toFloat())
            model.render(MatrixStack(), bufferBuilder, light, overlay)
        }
        val bufferBuilderFinal = bufferBuilder.end()
        buffer.bind()
        buffer.upload(bufferBuilderFinal)
        VertexBuffer.unbind()
    }

}
