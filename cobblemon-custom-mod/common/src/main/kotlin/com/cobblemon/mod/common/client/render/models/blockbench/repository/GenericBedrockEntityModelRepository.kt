/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.generic.JsonGenericPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import com.cobblemon.mod.common.util.cobblemonResource

object GenericBedrockEntityModelRepository : VaryingModelRepository<GenericBedrockEntity, PoseableEntityModel<GenericBedrockEntity>>() {
    override val title = "Generic"
    override val type = "generic"
    override val variationDirectories: List<String> = listOf("bedrock/$type/variations")
    override val poserDirectories: List<String> = listOf("bedrock/$type/posers")
    override val modelDirectories: List<String> = listOf("bedrock/$type/models")
    override val animationDirectories: List<String> = listOf("bedrock/$type/animations")
    override val isForLivingEntityRenderer = false

    override val fallback = cobblemonResource("substitute")

    override fun registerInBuiltPosers() {}
    override fun loadJsonPoser(json: String): (Bone) -> PoseableEntityModel<GenericBedrockEntity> {
        return {
            JsonGenericPoseableModel.JsonGenericPoseableModelAdapter.modelPart = it
            JsonGenericPoseableModel.gson.fromJson(json, JsonGenericPoseableModel::class.java)
        }
    }
}