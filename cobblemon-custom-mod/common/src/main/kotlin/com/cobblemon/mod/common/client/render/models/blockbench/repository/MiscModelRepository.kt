/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.client.render.models.blockbench.TexturedModel
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.client.model.ModelPart

import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * The data registry responsible for "one off" models that are usually used in BERs and don't really need their own repo
 * This is only present on the client.
 */
object MiscModelRepository : JsonDataRegistry<TexturedModel> {

    override val id = cobblemonResource("misc_models")
    override val type = ResourceType.CLIENT_RESOURCES
    override val observable = SimpleObservable<MiscModelRepository>()
    override val gson: Gson = TexturedModel.GSON
    override val typeToken: TypeToken<TexturedModel> = TypeToken.get(TexturedModel::class.java)
    override val resourcePath = "bedrock/misc"
    private val models = hashMapOf<Identifier, ModelPart>()

    override fun sync(player: ServerPlayerEntity) {}

    override fun reload(data: Map<Identifier, TexturedModel>) {
        data.forEach { (identifier, model) ->
            this.models[identifier] = model.create(false).createModel()
        }
        observable.emit(this)
        Cobblemon.LOGGER.info("Loaded {} misc models",this.models.size)
    }

    fun modelOf(identifier: Identifier) = this.models[identifier]
}
