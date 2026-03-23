/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.cosineFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class HydreigonModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("hydreigon")
    override val head = getPart("neck")

    override var portraitScale = 1.57F
    override var portraitTranslation = Vec3d(-1.04, 1.93, 0.0)

    override var profileScale = 0.58F
    override var profileTranslation = Vec3d(-0.06, 1.2, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

//    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("hydreigon", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("hydreigon", "blink") }

        val wingFrame1 = object : BiWingedFrame {
            override val rootPart = this@HydreigonModel.rootPart
            override val leftWing = getPart("wing_top_left")
            override val rightWing = getPart("wing_top_right")
        }

        val wingFrame2 = object : BiWingedFrame {
            override val rootPart = this@HydreigonModel.rootPart
            override val leftWing = getPart("wing_middle_left")
            override val rightWing = getPart("wing_middle_right")
        }

        val wingFrame3 = object : BiWingedFrame {
            override val rootPart = this@HydreigonModel.rootPart
            override val leftWing = getPart("wing_bottom_left")
            override val rightWing = getPart("wing_bottom_right")
        }

        standing = registerPose (
            poseName = "standing",
            quirks = arrayOf(blink),
                transformTicks = 10,
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hydreigon", "air_idle"),
                    wingFrame1.wingFlap(
                            flapFunction = sineFunction(verticalShift = -25F.toRadians(), period = 1.5F, amplitude = 0.8F),
                            timeVariable = { state, _, ageInTicks -> state?.animationSeconds ?: ageInTicks },
                            axis = ModelPartTransformation.Y_AXIS
                    ),
                    wingFrame2.wingFlap(
                            flapFunction = cosineFunction(verticalShift = -25F.toRadians(), period = 2F, amplitude = 0.65F),
                            timeVariable = { state, _, ageInTicks -> 0.01F + (state?.animationSeconds ?: (ageInTicks / 200)) },
                            axis = ModelPartTransformation.Y_AXIS
                    ),
                    wingFrame3.wingFlap(
                            flapFunction = sineFunction(verticalShift = -25F.toRadians(), period = 2F, amplitude = 0.5F),
                            timeVariable = { state, _, ageInTicks -> 0.01F + (state?.animationSeconds ?: (ageInTicks / 400)) },
                            axis = ModelPartTransformation.Y_AXIS
                    )
            )
        )

        walk = registerPose(
            poseName = "walk",
            quirks = arrayOf(blink),
                transformTicks = 10,
            poseTypes = PoseType.MOVING_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hydreigon", "air_fly"),
                    wingFrame1.wingFlap(
                            flapFunction = sineFunction(verticalShift = -25F.toRadians(), period = 2F, amplitude = 0.5F),
                            timeVariable = { state, _, ageInTicks -> state?.animationSeconds ?: ageInTicks },
                            axis = ModelPartTransformation.Y_AXIS
                    ),
                    wingFrame2.wingFlap(
                            flapFunction = cosineFunction(verticalShift = -25F.toRadians(), period = 2F, amplitude = 0.65F),
                            timeVariable = { state, _, ageInTicks -> 0.01F + (state?.animationSeconds ?: (ageInTicks / 20)) },
                            axis = ModelPartTransformation.Y_AXIS
                    ),
                    wingFrame3.wingFlap(
                            flapFunction = sineFunction(verticalShift = -25F.toRadians(), period = 1.5F, amplitude = 0.8F),
                            timeVariable = { state, _, ageInTicks -> 0.01F + (state?.animationSeconds ?: (ageInTicks / 40)) },
                            axis = ModelPartTransformation.Y_AXIS
                    )
                //bedrock("hydreigon", "ground_walk")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("hydreigon", "faint") else null
}