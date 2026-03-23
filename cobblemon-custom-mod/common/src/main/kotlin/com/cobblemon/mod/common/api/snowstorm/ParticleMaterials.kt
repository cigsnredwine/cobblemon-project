/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.snowstorm

import com.cobblemon.mod.common.client.render.shader.CobblemonShaders
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.render.*
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.texture.TextureManager

object ParticleMaterials {

    val ALPHA = object : ParticleTextureSheet {
        override fun begin(builder: BufferBuilder, textureManager: TextureManager) {
            RenderSystem.disableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_CUTOUT }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)


            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun draw(tessellator: Tessellator) {
            tessellator.draw()
        }

        override fun toString(): String {
            return "ALPHA"
        }
    }

    val ADD = object : ParticleTextureSheet {
        override fun begin(builder: BufferBuilder, textureManager: TextureManager) {
            RenderSystem.enableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_BLEND }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)

            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)

            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun draw(tessellator: Tessellator) {
            tessellator.draw()
        }

        override fun toString(): String {
            return "ADD"
        }
    }

    val BLEND = object : ParticleTextureSheet {
        override fun begin(builder: BufferBuilder, textureManager: TextureManager) {
            RenderSystem.enableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_BLEND }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)

            RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO)

            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun draw(tessellator: Tessellator) {
            tessellator.draw()
        }

        override fun toString(): String {
            return "BLEND"
        }
    }

    val OPAQUE = object : ParticleTextureSheet {
        override fun begin(builder: BufferBuilder, textureManager: TextureManager) {
            RenderSystem.enableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_BLEND }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)

            RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE)

            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun draw(tessellator: Tessellator) {
            tessellator.draw()
        }

        override fun toString(): String {
            return "OPAQUE"
        }
    }
}