/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class AnorithModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("anorith")

    override var portraitTranslation = Vec3d(-0.14, -1.56, 0.0)
    override var portraitScale = 1.8F

    override var profileTranslation = Vec3d(-0.05, -0.19, 0.0)
    override var profileScale = 1.03F

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var ui_poses: PokemonPose
    lateinit var battleidle: PokemonPose
    lateinit var water_surface_sleep: PokemonPose
    lateinit var water_sleep: PokemonPose
    lateinit var water_surface_idle: PokemonPose
    lateinit var water_idle: PokemonPose
    lateinit var water_surface_battleidle: PokemonPose
    lateinit var water_battleidle: PokemonPose
    lateinit var water_surface_swim: PokemonPose
    lateinit var water_swim:PokemonPose
    lateinit var shoulderLeft: PokemonPose
    lateinit var shoulderRight: PokemonPose

    val shoulderOffset = 5.5
    val wateroffset = -4.5
    val watersurfaceoffset = 1

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("anorith", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("anorith", "blink") }

        ui_poses = registerPose (
                poseName = "ui_poses",
                poseTypes = PoseType.UI_POSES,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "summary_idle")
                )
        )

        sleep = registerPose(
                poseName = "sleep",
                poseType = PoseType.SLEEP,
                condition = { !it.isTouchingWater },
                idleAnimations = arrayOf(bedrock("anorith", "sleep")
                )
        )

        water_surface_sleep = registerPose(
                poseName = "water_surface_sleep",
                poseType = PoseType.SLEEP,
                condition = { !it.isSubmergedInWater && it.isTouchingWater },
                idleAnimations = arrayOf(bedrock("anorith", "water_sleep")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, watersurfaceoffset)
                )
        )

        water_sleep = registerPose(
                poseName = "water_sleep",
                poseType = PoseType.SLEEP,
                condition = { it.isSubmergedInWater && it.isTouchingWater },
                idleAnimations = arrayOf(bedrock("anorith", "water_sleep")),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
                )
        )

        standing = registerPose(
                poseName = "standing",
                poseTypes = PoseType.STATIONARY_POSES,
                transformTicks = 10,
                condition = { !it.isBattling && !it.isTouchingWater},
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "ground_idle")
            )
        )

        water_surface_idle = registerPose(
                poseName = "water_surface_idle",
                poseTypes = PoseType.STATIONARY_POSES,
                condition = { !it.isSubmergedInWater && it.isTouchingWater && !it.isBattling},
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "water_idle")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, watersurfaceoffset)
                )
        )

        water_idle = registerPose(
                poseName = "water_idle",
                poseTypes = PoseType.STATIONARY_POSES,
                condition = { it.isSubmergedInWater && it.isTouchingWater && !it.isBattling},
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "water_idle")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
                )
        )

        battleidle = registerPose(
                poseName = "battle_idle",
                poseTypes = PoseType.STATIONARY_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink),
                condition = { it.isBattling && !it.isTouchingWater},
                idleAnimations = arrayOf(
                        bedrock("anorith", "battle_idle")
                )
        )

        water_surface_battleidle = registerPose(
                poseName = "surface_battle_idle",
                poseTypes = PoseType.STATIONARY_POSES,
                condition = { !it.isSubmergedInWater && it.isTouchingWater && it.isBattling},
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "water_battle_idle")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, watersurfaceoffset)
                )
        )

        water_battleidle = registerPose(
                poseName = "water_battle_idle",
                poseTypes = PoseType.STATIONARY_POSES,
                condition = { it.isSubmergedInWater && it.isTouchingWater && it.isBattling},
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "water_battle_idle")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
                )
        )

        walking = registerPose(
                poseName = "walking",
                poseTypes = PoseType.MOVING_POSES,
                quirks = arrayOf(blink),
                condition = { !it.isSubmergedInWater && !it.isTouchingWater },
                transformTicks = 10,
                idleAnimations = arrayOf(
                        bedrock("anorith", "ground_walk")
            )
        )

        water_surface_swim = registerPose(
                poseName = "surface_swim",
                poseTypes = PoseType.MOVING_POSES,
                quirks = arrayOf(blink),
                condition = { !it.isSubmergedInWater && it.isTouchingWater},
                transformTicks = 10,
                idleAnimations = arrayOf(
                        bedrock("anorith", "water_swim")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, watersurfaceoffset)
                )
        )

        water_swim = registerPose(
                poseName = "water_swim",
                poseTypes = PoseType.MOVING_POSES,
                quirks = arrayOf(blink),
                condition = { it.isSubmergedInWater && it.isTouchingWater },
                transformTicks = 10,
                idleAnimations = arrayOf(
                        bedrock("anorith", "water_swim")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
                )
        )

        shoulderLeft = registerPose(
                poseType = PoseType.SHOULDER_LEFT,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "shoulder_left")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.X_AXIS, shoulderOffset),
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, 2),
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Z_AXIS, 2)
                )
        )

        shoulderRight = registerPose(
                poseType = PoseType.SHOULDER_RIGHT,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        bedrock("anorith", "shoulder_right")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.X_AXIS, -shoulderOffset),
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, 2),
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Z_AXIS, 2)
                )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking)) bedrockStateful("anorith", "faint") else null
}