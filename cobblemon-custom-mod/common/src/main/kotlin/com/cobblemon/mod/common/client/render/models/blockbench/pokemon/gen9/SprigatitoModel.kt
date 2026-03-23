/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class SprigatitoModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("sprigatito")
    override val head = getPart("head")

    override var portraitScale = 2.1F
    override var portraitTranslation = Vec3d(-0.35, -0.7, 0.0)

    override var profileScale = 0.8F
    override var profileTranslation = Vec3d(0.0, 0.53, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("sprigatito", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("sprigatito", "blink") }
        val earTwitchRight = quirk(secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("sprigatito", "quirk_ear-twitch-left") }
        val earTwitchLeft = quirk(secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("sprigatito", "quirk_ear-twitch-right") }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            quirks = arrayOf(earTwitchRight, earTwitchLeft),
            idleAnimations = arrayOf(bedrock("sprigatito", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            condition = { !it.isBattling },
            quirks = arrayOf(blink, earTwitchRight, earTwitchLeft),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("sprigatito", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, earTwitchRight, earTwitchLeft),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("sprigatito", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, earTwitchRight, earTwitchLeft),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("sprigatito", "battle_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("sprigatito", "faint") else null
}