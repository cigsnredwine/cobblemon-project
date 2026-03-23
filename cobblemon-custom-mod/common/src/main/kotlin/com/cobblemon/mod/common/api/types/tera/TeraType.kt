/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.tera

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * The representation of the Pokémons Tera type.
 *
 * @see [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/Terastal_phenomenon)
 *
 */
interface TeraType : ShowdownIdentifiable {

    /**
     * The [Identifier] associated to this type.
     */
    val id: Identifier

    /**
     * If this tera type can be selected naturally.
     */
    val legalAsStatic: Boolean

    /**
     * The display name of this type.
     */
    val displayName: Text

}