/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.cobblemon.mod.common.api.dialogue.ArtificialDialogueFaceProvider
import com.cobblemon.mod.common.api.dialogue.DialogueFaceProvider
import com.cobblemon.mod.common.api.dialogue.ExpressionLikeDialogueFaceProvider
import com.cobblemon.mod.common.api.dialogue.PlayerDialogueFaceProvider
import com.cobblemon.mod.common.util.asExpressionLike
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object DialogueFaceProviderAdapter : JsonDeserializer<DialogueFaceProvider> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DialogueFaceProvider {
        return when (json) {
            is JsonPrimitive -> ExpressionLikeDialogueFaceProvider(json.asString.asExpressionLike())
            is JsonArray -> ExpressionLikeDialogueFaceProvider(json.asJsonArray.map { it.asString }.asExpressionLike())
            else -> context.deserialize(json, ArtificialDialogueFaceProvider::class.java)
        }
    }
}