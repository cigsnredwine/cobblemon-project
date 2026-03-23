/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen8

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ArctovishModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("arctovish")
    override val head = getPart("head")

    override var portraitScale = 0.66F
    override var portraitTranslation = Vec3d(-0.36, 1.98, 0.0)

    override var profileScale = 0.35F
    override var profileTranslation = Vec3d(0.0, 1.25, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var water_sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var float: PokemonPose
    lateinit var swim: PokemonPose
    lateinit var battleidle: PokemonPose
    lateinit var ui_poses: PokemonPose

    override val cryAnimation = CryProvider { _, pose ->
        when {
            pose.isPosedIn(float, swim ) -> bedrockStateful("arctovish", "water_cry")
            else -> bedrockStateful("arctovish", "cry")
        }
    }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("arctovish", "blink") }
        val waterquirk = quirk (secondsBetweenOccurrences = 30F to 60f) { bedrockStateful("arctovish", "water_quirk") }

        sleep = registerPose(
            poseName = "sleep",
            poseType = PoseType.SLEEP,
            condition = { !it.isTouchingWater },
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("arctovish", "sleep")
            )
        )

        water_sleep = registerPose(
                poseName = "water_sleep",
                poseType = PoseType.SLEEP,
                condition = { it.isTouchingWater },
                transformTicks = 10,
                idleAnimations = arrayOf(
                        bedrock("arctovish", "water_sleep")
                )
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES - PoseType.UI_POSES - PoseType.FLOAT,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("arctovish", "ground_idle")
            )
        )

        ui_poses = registerPose(
                poseName = "ui_poses",
                poseTypes = PoseType.UI_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("arctovish", "water_idle")
                )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES - PoseType.SWIM,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("arctovish", "ground_walk")
            )
        )

        float = registerPose(
            poseName = "float",
            poseType = PoseType.FLOAT,
            transformTicks = 10,
            quirks = arrayOf(blink, waterquirk),
            idleAnimations = arrayOf(
                bedrock("arctovish", "water_idle")
            )
        )

        swim = registerPose(
            poseName = "swim",
            poseType = PoseType.SWIM,
            transformTicks = 10,
            quirks = arrayOf(blink, waterquirk),
            idleAnimations = arrayOf(
                bedrock("arctovish", "water_swim")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("arctovish", "faint") else null
}