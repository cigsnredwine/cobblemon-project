/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.client.CobblemonBakingOverrides;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.points.BeforeStringInvoke;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow protected abstract void addModel(ModelIdentifier modelId);

    @Final
    @Shadow
    private Map<Identifier, UnbakedModel> unbakedModels;

    @Final
    @Shadow
    private static Logger LOGGER;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    public void init(BlockColors blockColors,
        Profiler profiler,
        Map jsonUnbakedModels,
        Map blockStates,
        CallbackInfo ci) {
        CobblemonBakingOverrides.INSTANCE.getModels().forEach(bakingOverride -> {
            try {
                this.unbakedModels.put(bakingOverride.getModelIdentifier(), this.loadModelFromJson(bakingOverride.getModelLocation()));
                this.addModel(bakingOverride.getModelIdentifier());
            } catch (IOException e) {
                LOGGER.error("Error loading a Cobblemon BakedModel:", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Shadow
    private JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException {
        return null;
    }
}
