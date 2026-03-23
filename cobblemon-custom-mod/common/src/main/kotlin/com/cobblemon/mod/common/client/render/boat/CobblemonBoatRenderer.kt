/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.boat

import com.cobblemon.mod.common.entity.boat.CobblemonBoatEntity
import com.cobblemon.mod.common.entity.boat.CobblemonBoatType
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.BoatEntityModel
import net.minecraft.client.render.entity.model.ChestBoatEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Quaternionf

class CobblemonBoatRenderer(ctx: EntityRendererFactory.Context, private val hasChest: Boolean) : EntityRenderer<CobblemonBoatEntity>(ctx) {

    private val boatModels = hashMapOf<CobblemonBoatType, Pair<Identifier, BoatEntityModel>>()

    init {
        this.shadowRadius = 0.8F
        CobblemonBoatType.values().forEach { type ->
            this.boatModels[type] = generateTextureIdentifier(type, this.hasChest) to generateBoatModel(ctx, type, this.hasChest)
        }
    }

    override fun getTexture(entity: CobblemonBoatEntity): Identifier = this.boatModels[entity.boatType]!!.first

    override fun render(entity: CobblemonBoatEntity, yaw: Float, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int) {
        matrices.push()
        matrices.translate(0F, 0.375F, 0F)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F - yaw))
        val h = entity.damageWobbleTicks - tickDelta
        val j = (entity.damageWobbleStrength - tickDelta).coerceAtLeast(0F)
        if (h > 0F) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(h) * h * j / 10F * entity.damageWobbleSide))
        }
        val k = entity.interpolateBubbleWobble(tickDelta)
        if (!MathHelper.approximatelyEquals(k, 0F)) {
            matrices.multiply(Quaternionf().setAngleAxis(entity.interpolateBubbleWobble(tickDelta) * 0.017453292F, 1F, 0F, 1F))
        }
        val (identifier, entityModel) = this.boatModels[entity.boatType]!!
        matrices.scale(-1F, -1F, 1F)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90F))
        entityModel.setAngles(entity, tickDelta, 0F, -0.1F, 0F, 0F)
        val vertexConsumer = vertexConsumers.getBuffer(entityModel.getLayer(identifier))
        entityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
        if (!entity.isSubmergedInWater) {
            val vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getWaterMask())
            entityModel.waterPatch.render(matrices, vertexConsumer2, light, OverlayTexture.DEFAULT_UV)
        }
        matrices.pop()
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light)
    }

    companion object {

        private fun generateTextureIdentifier(type: CobblemonBoatType, hasChest: Boolean): Identifier {
            val boatSubPath = if (hasChest) "chest_boat" else "boat"
            val path = "textures/entity/$boatSubPath/${type.name.lowercase()}.png"
            return cobblemonResource(path)
        }

        private fun generateBoatModel(ctx: EntityRendererFactory.Context, type: CobblemonBoatType, hasChest: Boolean): BoatEntityModel {
            val modelLayer = this.createBoatModelLayer(type, hasChest)
            val modelPart = ctx.getPart(modelLayer)
            return if (hasChest) ChestBoatEntityModel(modelPart) else BoatEntityModel(modelPart)
        }

        internal fun createBoatModelLayer(type: CobblemonBoatType, hasChest: Boolean): EntityModelLayer {
            val boatSubPath = if (hasChest) "chest_boat" else "boat"
            val path = "$boatSubPath/${type.name.lowercase()}"
            return EntityModelLayer(cobblemonResource(path), "main")
        }

    }

}