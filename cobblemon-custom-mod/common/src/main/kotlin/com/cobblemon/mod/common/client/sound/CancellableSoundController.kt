/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.sound

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.SoundInstance
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

object CancellableSoundController {

    private val manager = MinecraftClient.getInstance().soundManager
    private val playingSounds: MutableMap<BlockPos, MutableMap<Identifier, SoundInstance>> = emptyMap<BlockPos, MutableMap<Identifier, SoundInstance>>().toMutableMap()

    /** Start a new  and pause all [filteredCategories] sounds currently playing. */
    fun playSound(newSound: CancellableSoundInstance) {
        manager.play(newSound)

        var idMap = playingSounds[newSound.pos]
        var soundInstance : SoundInstance? = null
        if(idMap == null) {
            idMap = emptyMap<Identifier, SoundInstance>().toMutableMap()
        } else {
            soundInstance = idMap[newSound.id]
        }
        if(soundInstance != null) {
            manager.stop(soundInstance)
        }
        idMap[newSound.id] = newSound
        playingSounds[newSound.pos] = idMap
    }

    fun stopSound(soundInstance: CancellableSoundInstance) {
        this.stopSound(soundInstance.pos, soundInstance.sound.identifier)//import com.cobblemon.mod.common.client.sound.com.cobblemon.mod.common.client.sound.CancellableSoundInstance

    }
    fun stopSound(blockPos: BlockPos, identifier: Identifier) {
        val idMap = playingSounds.get(blockPos)
        if(idMap != null) {
            val soundInstance = idMap.get(identifier)
            if(soundInstance !== null) {
                manager.stop(soundInstance)
                idMap.remove(identifier)
                if(idMap.keys.size == 0) {
                    playingSounds.remove(blockPos)
                }
            }
        }
    }
}