/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.client.sound.battle.BattleMusicController;
import com.cobblemon.mod.common.duck.SoundSystemDuck;
import com.google.common.collect.Multimap;
import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin implements SoundSystemDuck {

    @Shadow
    private boolean started;

    @Shadow
    private Multimap<SoundCategory, SoundInstance> sounds;

    @Shadow @Final
    private Map<SoundInstance, Channel.SourceManager> sources;

    @Shadow
    protected abstract void stop(SoundInstance sound);

    @Shadow
    public abstract boolean isPlaying(SoundInstance sound);

    /** Resumes the Source belonging to the queried SoundInstance. */
    private void resume(SoundInstance sound) {
        Channel.SourceManager sourceManager = this.sources.get(sound);
        if (this.started && sourceManager != null) sourceManager.run(Source::resume);
    }

    /** Pauses the Source belonging to the queried SoundInstance. */
    private void pause(SoundInstance sound) {
        Channel.SourceManager sourceManager = this.sources.get(sound);
        if (this.started && sourceManager != null) sourceManager.run(Source::pause);
    }

    /** Resumes the SoundInstance(s) queried by id and/or category. */
    @Override
    public void resumeSounds(@Nullable Identifier id, @Nullable SoundCategory category) {
        if (category != null) {
            this.sounds.get(category).forEach((sound) -> {
                if (id == null || sound.getId().equals(id)) resume(sound);
            });
        } else if (id == null) {
            this.sources.keySet().forEach(this::resume);
        } else {
            this.sources.keySet().forEach(sound -> {
                if (sound.getId().equals(id)) resume(sound);
            });
        }
    }

    /** Pauses the SoundInstances queried by id and/or category. */
    @Override
    public void pauseSounds(@Nullable Identifier id, @Nullable SoundCategory category) {
        if (category != null) {
            this.sounds.get(category).forEach((sound) -> {
                if (id == null || sound.getId().equals(id)) pause(sound);
            });
        } else if (id == null) {
            this.sources.keySet().forEach(this::pause);
        } else {
            this.sources.keySet().forEach(sound -> {
                if (sound.getId().equals(id)) pause(sound);
            });
        }
    }

    /** Allows stopping all SoundInstances that belong to a queried category. */
    @Inject(method = "stopSounds(Lnet/minecraft/util/Identifier;Lnet/minecraft/sound/SoundCategory;)V", at = @At("HEAD"), cancellable = true)
    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory category, CallbackInfo cb) {
        if (id == null && category != null) {
            this.sounds.get(category).forEach(this::stop);
            cb.cancel();
        }
    }

    /** Different behavior for resuming SoundInstances while a BattleMusicInstance is being played. We do not want to resume filtered sounds. */
    @Inject(method = "resumeAll()V", at = @At("HEAD"), cancellable = true)
    public void resumeAll(CallbackInfo cb) {
        if (this.isPlaying(BattleMusicController.INSTANCE.getMusic())) {
            this.sounds.values().forEach(sound -> {
               if (sound == BattleMusicController.INSTANCE.getMusic() || !BattleMusicController.INSTANCE.getFilteredCategories().contains(sound.getCategory())) {
                   resume(sound);
               }
            });
            cb.cancel();
        }
    }
}
