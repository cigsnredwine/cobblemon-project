/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.blockentity

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.Entity

class BlockEntityModel(val root: Bone) : PoseableEntityModel<Entity>() {
    val boneName: String = root.children.entries.first().key
    override val rootPart = (root as ModelPart).registerChildWithAllChildren(boneName)

    override val isForLivingEntityRenderer = false
    var idleAnimations: Array<StatelessAnimation<Entity, out ModelFrame>> = emptyArray()
    var maxScale = 1F
    var yTranslation = 0F
    override fun registerPoses() {
        val closedPose = registerPose<ModelFrame>(poseName = "CLOSED", poseType = PoseType.NONE)
        val openPose = registerPose(
            poseType = PoseType.OPEN,
            idleAnimations = arrayOf(bedrock("gilded_chest", "open"))
        )

        closedPose.transitions[openPose.poseName] = { _, _ ->
            bedrockStateful("gilded_chest", "opening")
        }

        openPose.transitions[closedPose.poseName] = { _, _ ->
            bedrockStateful("gilded_chest", "closing")
        }
    }

    override fun getState(entity: Entity) = throw NotImplementedError("This is not supported for the gilded chest")
}