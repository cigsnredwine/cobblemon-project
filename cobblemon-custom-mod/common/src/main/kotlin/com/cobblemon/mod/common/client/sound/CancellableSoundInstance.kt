/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.sound

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.*
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class CancellableSoundInstance(sound: SoundEvent, pos:BlockPos = BlockPos.ORIGIN, repeat: Boolean = false, volume: Float = 1.0F, pitch: Float = 1.0F,  ) :
        PositionedSoundInstance(sound, SoundCategory.BLOCKS, volume, pitch, SoundInstance.createRandom(), pos), TickableSoundInstance {

    private val soundManager = MinecraftClient.getInstance().soundManager;
    private var done: Boolean = false
    private var unheardTicks = 0
    private var initVolume = 1.0
    public var pos : BlockPos

    init {
        this.relative = false
        this.repeat = repeat
        this.attenuationType = SoundInstance.AttenuationType.NONE
        this.initVolume = volume.toDouble()
        this.pos = pos
        this.attenuationType = SoundInstance.AttenuationType.LINEAR
    }

    override fun isDone(): Boolean {
        return this.done
    }

    override fun tick() {
        if(soundManager.isPlaying(this) && !this.repeat) {
            this.done = true
            CancellableSoundController.stopSound(this)
        } else {
            // Using the player's position as a proxy for a SoundListener.
            val listenerPos = MinecraftClient.getInstance().player?.pos?.squaredDistanceTo(Vec3d(this.x, this.y, this.z))
            if(listenerPos != null) {
                if(listenerPos > ATTENUATION_DISTANCE_MAX_SQUARED * 2) {
                    // listener is very far away, kill it
                    this.done = true
                    CancellableSoundController.stopSound(this)
                } else if( listenerPos > ATTENUATION_DISTANCE_MAX_SQUARED) {
                    ++unheardTicks
                    // Purpose of this is if a client is pushing in and out of the attenuation range, they won't notice the sound abruptly stopping
                    if(unheardTicks > UNHEARD_TICKS_MAX) {
                        this.done = true
                        this.repeat = false
                        CancellableSoundController.stopSound(this)
                    }
                }
            } else {
                unheardTicks = 0
            }
        }
    }

    companion object {
        const val UNHEARD_TICKS_MAX = 200
        const val ATTENUATION_DISTANCE_MAX_SQUARED = 18 * 18
    }
}
