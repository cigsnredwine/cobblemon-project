/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.generic

import com.cobblemon.mod.common.client.entity.GenericBedrockClientDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.JsonPoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import com.cobblemon.mod.common.util.adapters.Vec3dAdapter
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.function.Supplier
import net.minecraft.util.math.Vec3d

class JsonGenericPoseableModel(override val rootPart: Bone, override val isForLivingEntityRenderer: Boolean = false) : JsonPoseableEntityModel<GenericBedrockEntity>(rootPart) {

    object JsonGenericPoseableModelAdapter : InstanceCreator<JsonGenericPoseableModel> {
        var modelPart: Bone? = null
        var model: JsonGenericPoseableModel? = null
        override fun createInstance(type: Type): JsonGenericPoseableModel {
            return JsonGenericPoseableModel(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    companion object {
        var model: JsonGenericPoseableModel? = null
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Vec3d::class.java, Vec3dAdapter)
            .setExclusionStrategies(JsonModelExclusion)
            .registerTypeAdapter(
                TypeToken.getParameterized(
                    Supplier::class.java,
                    TypeToken.getParameterized(
                        StatefulAnimation::class.java,
                        GenericBedrockEntity::class.java,
                        ModelFrame::class.java
                    ).type
                ).type,
                StatefulAnimationAdapter { JsonGenericPoseableModelAdapter.model!! }
            )
            .registerTypeAdapter(Pose::class.java, PoseAdapter { JsonGenericPoseableModelAdapter.model!! })
            .registerTypeAdapter(
                JsonGenericPoseableModel::class.java,
                JsonGenericPoseableModelAdapter
            )
            .create()
    }

    override fun getState(entity: GenericBedrockEntity) = entity.delegate as GenericBedrockClientDelegate
}