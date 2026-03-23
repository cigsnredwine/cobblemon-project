/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.client.render.atlas.CobblemonAtlases
import com.cobblemon.mod.common.client.render.models.blockbench.TexturedModel
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.client.model.ModelPart

import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * The data registry responsible for berry fruit and flower models.
 * This is only present on the client.
 */
object BerryModelRepository : JsonDataRegistry<TexturedModel> {

    override val id = cobblemonResource("berry_models")
    override val type = ResourceType.CLIENT_RESOURCES
    override val observable = SimpleObservable<BerryModelRepository>()
    override val gson: Gson = TexturedModel.GSON
    override val typeToken: TypeToken<TexturedModel> = TypeToken.get(TexturedModel::class.java)
    override val resourcePath = "bedrock/berries"
    private val rawModels = hashMapOf<Identifier, TexturedModel>()
    private val processedModels = hashMapOf<Identifier, ModelPart>()

    override fun sync(player: ServerPlayerEntity) {}

    override fun reload(data: Map<Identifier, TexturedModel>) {
        data.forEach { (identifier, model) ->
            this.rawModels[identifier] = model
        }
        observable.emit(this)
        Cobblemon.LOGGER.info("Loaded {} berry models",this.rawModels.size)
    }

    //This runs after the berry atlas + berry registry has been created, hotfixes models to use the u-v vals of the atlas
    fun patchModels() {
        Berries.all().forEach {
            val fruitModel = rawModels[it.fruitModelIdentifier]
            val flowerModel = rawModels[it.flowerModelIdentifier]
            val fruitTexId = it.fruitTexture
            val flowerTexId = it.flowerTexture
            val fruitTex = CobblemonAtlases.BERRY_SPRITE_ATLAS.getSprite(fruitTexId)
            val flowerTex = CobblemonAtlases.BERRY_SPRITE_ATLAS.getSprite(flowerTexId)
            processedModels[it.fruitModelIdentifier] = fruitModel?.createWithUvOverride(
                false,
                fruitTex.x,
                fruitTex.y,
                CobblemonAtlases.BERRY_SPRITE_ATLAS.atlas.width,
                CobblemonAtlases.BERRY_SPRITE_ATLAS.atlas.height
            )?.createModel()!!
            processedModels[it.flowerModelIdentifier] = flowerModel?.createWithUvOverride(
                false,
                flowerTex.x,
                flowerTex.y,
                CobblemonAtlases.BERRY_SPRITE_ATLAS.atlas.width,
                CobblemonAtlases.BERRY_SPRITE_ATLAS.atlas.height
            )?.createModel()!!
        }
    }

    fun modelOf(identifier: Identifier) = this.processedModels[identifier]
}