/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.api.entity.EntitySideDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.GenericBedrockEntityModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity

class GenericBedrockClientDelegate : EntitySideDelegate<GenericBedrockEntity>, PoseableEntityState<GenericBedrockEntity>() {
    lateinit var currentEntity: GenericBedrockEntity
    override val schedulingTracker
        get() = getEntity().schedulingTracker
    override fun getEntity() = currentEntity

    override fun initialize(entity: GenericBedrockEntity) {
        super.initialize(entity)
        this.currentEntity = entity
        this.age = entity.age
        this.currentModel = GenericBedrockEntityModelRepository.getPoser(entity.category, entity.aspects)

        val model = currentModel!!
        model.context.put(RenderContext.ENTITY, entity)
        currentModel!!.updateLocators(this)
        updateLocatorPosition(entity.pos)

        val currentPoseType = entity.getCurrentPoseType()
        val pose = this.currentModel!!.poses.values.firstOrNull { currentPoseType in it.poseTypes && (it.condition == null || it.condition.invoke(entity)) }
        if (pose != null) {
            doLater { setPose(pose.poseName) }
        }
    }

    override fun tick(entity: GenericBedrockEntity) {
        super.tick(entity)
        updateLocatorPosition(entity.pos)
        incrementAge(entity)
    }

    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks = partialTicks
    }
}