/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.client.render.models.blockbench.blockentity.BlockEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.JsonPokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.fromJson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.minecraft.entity.Entity

/**
 * This is specifically for BlockEntities where we want to do more complex animation than what Java Edition already allows
 * Basically with Java Edition, we can only change the texture applied to a model, if we want to move bones around, need to
 * do something else, so we use this
 */
object BlockEntityModelRepository : VaryingModelRepository<Entity, BlockEntityModel>() {
    override val title = "Block Entity"
    override val type = "block_entities"
    override val variationDirectories: List<String> = listOf("bedrock/$type/variations", "bedrock/$type")
    override val poserDirectories: List<String> = listOf("bedrock/$type/posers")
    override val modelDirectories: List<String> = listOf("bedrock/$type/models")
    override val animationDirectories: List<String> = listOf("bedrock/$type/animations")
    override val fallback = cobblemonResource("substitute")
    override val isForLivingEntityRenderer = false

    private val gson = GsonBuilder().create()
    override fun loadJsonPoser(json: String): (Bone) -> BlockEntityModel {
        val jsonObject = gson.fromJson<JsonObject>(json)
        val animations = jsonObject.getAsJsonArray("animations")
        val maxScale = jsonObject.get("maxScale")?.asFloat ?: 1F
        val yTranslation = jsonObject.get("yTranslation")?.asFloat ?: 0F
        return { bone ->
            val model = BlockEntityModel(bone)
            model.maxScale = maxScale
            model.yTranslation = yTranslation
            // I stole all this from FossilModelRepository, so I guess I'll leave the comments here
            //
            // Refactor this bullshit to not mention pokemon at all, it should be common to anything using animation factories.
            // Even better: move to molang functions, this is ass
            model.idleAnimations = animations.mapNotNull {
                val animString = it.asString
                val anim = animString.substringBefore("(")
                if (JsonPokemonPoseableModel.ANIMATION_FACTORIES.contains(anim)) {
                    return@mapNotNull JsonPokemonPoseableModel.ANIMATION_FACTORIES[anim]!!.stateless(model, animString)
                } else {
                    null
                }
            }.toTypedArray()
            model
        }
    }

    override fun registerInBuiltPosers() {}
}