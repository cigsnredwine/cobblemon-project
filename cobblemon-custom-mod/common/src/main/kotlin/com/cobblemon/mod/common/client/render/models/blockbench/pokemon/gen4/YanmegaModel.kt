/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4

import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.triangleFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class YanmegaModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("yanmega")
    override val head = getPart("head")

    override var portraitScale = 1.6F
    override var portraitTranslation = Vec3d(-0.55, -0.9, 0.0)

    override var profileScale = 0.61F
    override var profileTranslation = Vec3d(0.0, 0.8, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var flying:PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("yanmega", "cry") }

    override fun registerPoses() {
        val wingFrame1 = object : BiWingedFrame {
            override val rootPart = this@YanmegaModel.rootPart
            override val leftWing = getPart("wing_left1")
            override val rightWing = getPart("wing_right1")
        }

        val wingFrame2 = object : BiWingedFrame {
            override val rootPart = this@YanmegaModel.rootPart
            override val leftWing = getPart("wing_left2")
            override val rightWing = getPart("wing_right2")
        }

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES - PoseType.HOVER,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("yanmega", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES - PoseType.FLY,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("yanmega", "air_idle"),
                wingFrame1.wingFlap(
                    flapFunction = triangleFunction(period = 0.1F, amplitude = 0.5F),
                    timeVariable = { state, _, ageInTicks -> state?.animationSeconds ?: ageInTicks },
                    axis = ModelPartTransformation.Z_AXIS
                ),
                wingFrame2.wingFlap(
                    flapFunction = triangleFunction(period = 0.1F, amplitude = 0.5F),
                    timeVariable = { state, _, ageInTicks -> 0.01F + (state?.animationSeconds ?: (ageInTicks / 20)) },
                    axis = ModelPartTransformation.Z_AXIS
                )
                //bedrock("yanmega", "ground_walk")
            ),
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, -4)
            )
        )

        hover = registerPose(
            poseName = "hovering",
            poseType = PoseType.HOVER,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("yanmega", "air_idle"),
                wingFrame1.wingFlap(
                    flapFunction = triangleFunction(period = 0.1F, amplitude = 0.5F),
                    timeVariable = { state, _, ageInTicks -> state?.animationSeconds ?: ageInTicks },
                    axis = ModelPartTransformation.Z_AXIS
                ),
                wingFrame2.wingFlap(
                    flapFunction = triangleFunction(period = 0.1F, amplitude = 0.5F),
                    timeVariable = { state, _, ageInTicks -> 0.01F + (state?.animationSeconds ?: (ageInTicks / 20)) },
                    axis = ModelPartTransformation.Z_AXIS
                )
            ),
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, -4)
            )
        )

        flying = registerPose(
            poseName = "flying",
            poseType = PoseType.FLY,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("yanmega", "air_idle"),
                wingFrame1.wingFlap(
                    flapFunction = triangleFunction(period = 0.1F, amplitude = 0.5F),
                    timeVariable = { state, _, ageInTicks -> state?.animationSeconds ?: ageInTicks },
                    axis = ModelPartTransformation.Z_AXIS
                ),
                wingFrame2.wingFlap(
                    flapFunction = triangleFunction(period = 0.1F, amplitude = 0.5F),
                    timeVariable = { state, _, ageInTicks -> 0.01F + (state?.animationSeconds ?: (ageInTicks / 20)) },
                    axis = ModelPartTransformation.Z_AXIS
                )
                //bedrock("yanmega", "ground_walk")
            ),
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, -4)
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("yanmega", "faint") else null
}