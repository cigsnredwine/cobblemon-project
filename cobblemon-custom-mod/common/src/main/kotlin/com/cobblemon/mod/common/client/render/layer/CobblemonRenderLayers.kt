/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.layer

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase.*
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import java.util.function.BiFunction
import java.util.function.Function

object CobblemonRenderLayers {
    val BERRY_LAYER = run {
        val multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .lightmap(ENABLE_LIGHTMAP)
            .program(CUTOUT_PROGRAM)
            .texture(Texture(
                cobblemonResource("textures/atlas/berries.png"),
                false,
                true
            ))
            .cull(DISABLE_CULLING)
            .build(true)
        RenderLayer.of(
            "berries",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            512,
            true,
            false,
            multiPhaseParameters
        )
    }

    val ENTITY_TRANSLUCENT: BiFunction<Identifier, Boolean, RenderLayer> = Util.memoize { texture: Identifier, affectsOutline: kotlin.Boolean ->
        var multiPhaseParameters: RenderLayer.MultiPhaseParameters =
            RenderLayer.MultiPhaseParameters.builder()
                .program(ENTITY_TRANSLUCENT_PROGRAM)
                .texture(Texture(texture, false, false))
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .cull(DISABLE_CULLING)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR).build(affectsOutline)
        RenderLayer.of(
            "entity_translucent",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            256,
            true,
            true,
            multiPhaseParameters
        )
    };

    val ENTITY_CUTOUT: Function<Identifier, RenderLayer> = Util.memoize { texture: Identifier ->
        val multiPhaseParameters =
            RenderLayer.MultiPhaseParameters.builder()
                .program(ENTITY_CUTOUT_PROGRAM)
                .texture(Texture(texture, false, false))
                .transparency(NO_TRANSPARENCY)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR).build(true)
        RenderLayer.of(
            "entity_cutout",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            256,
            true,
            false,
            multiPhaseParameters
        )
    };

}