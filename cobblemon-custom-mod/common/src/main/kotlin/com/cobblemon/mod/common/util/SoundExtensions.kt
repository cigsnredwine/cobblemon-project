/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import com.cobblemon.mod.common.duck.SoundManagerDuck
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.sound.Source
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier

/**
 * Pauses the audio [Source]s belonging to the queried [SoundInstance]s.
 *
 * @param id The [Identifier] of the [SoundInstance] to pause. If null, will pause all sounds belonging to the specified [category].
 * @param category The [SoundCategory] that [id] is queried from. If null, will query [id] from all categories.
 */
fun SoundManager.pauseSounds(id: Identifier?, category: SoundCategory?) = (this as SoundManagerDuck).pauseSounds(id, category)

/**
 * Resumes the audio [Source]s belonging to the queried [SoundInstance]s.
 *
 * @param id The [Identifier] of the [SoundInstance] to resume. If null, will resume all sounds belonging to the specified [category].
 * @param category The [SoundCategory] that [id] is queried from. If null, will query [id] from all categories.
 */
fun SoundManager.resumeSounds(id: Identifier?, category: SoundCategory?) = (this as SoundManagerDuck).resumeSounds(id, category)
