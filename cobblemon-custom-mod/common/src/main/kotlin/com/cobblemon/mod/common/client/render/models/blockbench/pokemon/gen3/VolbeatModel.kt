/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class VolbeatModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("volbeat")
    override val leftWing = getPart("left_wing")
    override val rightWing = getPart("right_wing")
    override val leftLeg = getPart("left_leg")
    override val rightLeg = getPart("right_leg")
    override val head = getPart("head")

    override var portraitScale = 2.0F
    override var portraitTranslation = Vec3d(-0.2, -0.3, 0.0)

    override var profileScale = 0.9F
    override var profileTranslation = Vec3d(0.0, 0.3, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var water_surface_idle: PokemonPose
    lateinit var water_surface_fly: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var battleidle: PokemonPose

    val wateroffset = -15

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("illumise", "blink") }
        val flicker = quirk { bedrockStateful("illumise", "flicker_quirk") }

        sleep = registerPose(
            poseName = "sleep",
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("volbeat", "sleep"))
        )

        water_surface_idle = registerPose(
            poseName = "water_surface",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, flicker),
            condition = { it.isTouchingWater && !it.isSubmergedInWater },
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "air_idle")
            )
        )

        water_surface_fly = registerPose(
            poseName = "water_surface_fly",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, flicker),
            condition = { it.isTouchingWater && !it.isSubmergedInWater },
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "air_fly")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseTypes = setOf(PoseType.HOVER),
            transformTicks = 10,
            quirks = arrayOf(blink, flicker),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseTypes = setOf(PoseType.FLY),
            transformTicks = 10,
            quirks = arrayOf(blink, flicker),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "air_fly")
            )
        )

        stand = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, flicker),
            condition = { !it.isBattling},
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseType = PoseType.WALK,
            transformTicks = 10,
            quirks = arrayOf(blink, flicker),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, flicker),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "battle_idle")
            )
        )
    }
}