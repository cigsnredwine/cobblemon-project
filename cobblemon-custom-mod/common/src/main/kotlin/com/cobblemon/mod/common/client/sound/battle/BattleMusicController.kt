/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.sound.battle

import com.cobblemon.mod.common.util.pauseSounds
import com.cobblemon.mod.common.util.resumeSounds
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

/**
 * The controller for playing, switching, and stopping [BattleMusicInstance]s.
 *
 * @author Segfault Guy
 * @since October 31st, 2023
 */
object BattleMusicController {

    /** The last [BattleMusicInstance] played. */
    var music = BattleMusicInstance(SoundEvents.INTENTIONALLY_EMPTY, 0.0F, 0.0F)
        private set

    /**
     * The [SoundCategory]s that are filtered while the [SoundManager] is playing a [BattleMusicInstance].
     *
     * Applicable [SoundInstance]s are blocked while a [BattleMusicInstance] is active, or paused if played before.
     */
    val filteredCategories = listOf(SoundCategory.AMBIENT, SoundCategory.MUSIC, SoundCategory.RECORDS)

    private val manager = MinecraftClient.getInstance().soundManager

    /** Start a new [BattleMusicInstance] and pause all [filteredCategories] sounds currently playing. */
    fun initializeMusic(newMusic: BattleMusicInstance) {
        music = newMusic
        manager.play(music)
        if (manager.isPlaying(music)) {
            filteredCategories.forEach { manager.pauseSounds(null, it) }
            manager.resumeSounds(music.id, SoundCategory.MUSIC) // lazy :)
        }
    }

    /** Switch to a new [BattleMusicInstance] if one is already playing. */
    fun switchMusic(newMusic: BattleMusicInstance) {
        manager.stop(music)
        music = newMusic
        manager.play(music)
    }

    /** Fade out and end the current [BattleMusicInstance]. */
    fun endMusic() = music.setFade()

}
