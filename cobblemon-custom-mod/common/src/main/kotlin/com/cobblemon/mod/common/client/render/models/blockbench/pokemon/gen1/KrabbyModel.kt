/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.util.isDusk
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class KrabbyModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("krabby")

    override var portraitScale = 2.5F
    override var portraitTranslation = Vec3d(-0.15, -1.8, 0.0)

    override var profileScale = 1.0F
    override var profileTranslation = Vec3d(0.0, 0.2, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var battleidle: PokemonPose
    lateinit var standingBubbles: PokemonPose
    lateinit var standingRain: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("krabby", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("krabby", "blink")}
        val snipLeft = quirk { bedrockStateful("krabby", "snip_left")}
        val snipRight = quirk { bedrockStateful("krabby", "snip_right")}
        val bubble = quirk(10F to 20F) { bedrockStateful("krabby", "quirk_bubble")}

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            condition = { !it.isBattling && !it.isDusk() },
            quirks = arrayOf(blink, snipLeft, snipRight ),
            idleAnimations = arrayOf(
                bedrock("krabby", "ground_idle")
            )
        )

        standingBubbles = registerPose(
            poseName = "standing_bubbles",
            poseTypes = STATIONARY_POSES,
            condition = { !it.isBattling && it.isDusk() && !it.isTouchingWaterOrRain },
            quirks = arrayOf(blink, snipLeft, snipRight, bubble),
            idleAnimations = arrayOf(
                bedrock("krabby", "ground_idle")
            )
        )

        standingRain = registerPose(
            poseName = "standing_rain",
            poseTypes = STATIONARY_POSES,
            condition = { !it.isBattling && it.isDusk() && it.isTouchingWaterOrRain},
            quirks = arrayOf(blink, snipLeft, snipRight),
            idleAnimations = arrayOf(
                bedrock("krabby", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            quirks = arrayOf(blink, snipLeft, snipRight),
            idleAnimations = arrayOf(
                bedrock("krabby", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, snipLeft, snipRight),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                bedrock("krabby", "battle_idle")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("krabby", "faint") else null
}