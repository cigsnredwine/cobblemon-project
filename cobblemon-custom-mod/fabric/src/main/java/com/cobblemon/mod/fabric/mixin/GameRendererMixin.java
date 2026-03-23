/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.mixin;

import com.cobblemon.mod.common.client.render.shader.CobblemonShaders;
import com.cobblemon.mod.common.util.ShaderRegistryData;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {


    @Inject(method = "loadPrograms", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void lodestone$registerShaders(ResourceFactory factory, CallbackInfo ci, List<ShaderStage> list, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2) throws IOException {
        CobblemonShaders.INSTANCE.init();
        CobblemonShaders.INSTANCE.getSHADERS_TO_REGISTER().forEach((pair) -> {
            ShaderRegistryData data = pair.getFirst().invoke(factory);
            FabricShaderProgram program = null;
            try {
                program = new FabricShaderProgram(data.getResourceFactory(), data.getShaderName(), data.getVertexFormat());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            list2.add(Pair.of(program, pair.getSecond()));
        });
    }
}
