/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DodrioModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("dodrio")
    override val head = getPart("head4")

    val lefthead = object : HeadedFrame {
        override val rootPart = this@DodrioModel.rootPart
        override val head: ModelPart = getPart("head3")
    }
    val righthead = object : HeadedFrame {
        override val rootPart = this@DodrioModel.rootPart
        override val head: ModelPart = getPart("head2")
    }

    override var portraitScale = 1.5F
    override var portraitTranslation = Vec3d(-0.15, 0.9, 0.0)

    override var profileScale = 0.8F
    override var profileTranslation = Vec3d(0.0, 0.6, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("dodrio", "cry") }

    override fun registerPoses() {
        val blink1 = quirk { bedrockStateful("dodrio", "blink1") }
        val blink2 = quirk { bedrockStateful("dodrio", "blink2") }
        val blink3 = quirk { bedrockStateful("dodrio", "blink3") }
        val bite1 = quirk(secondsBetweenOccurrences = 5F to 20F) { bedrockStateful("dodrio", "bite_quirk1") }
        val bite2 = quirk(secondsBetweenOccurrences = 5F to 20F) { bedrockStateful("dodrio", "bite_quirk2") }
        val bite3 = quirk(secondsBetweenOccurrences = 5F to 20F) { bedrockStateful("dodrio", "bite_quirk3") }


        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("dodrio", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            transformTicks = 10,
            condition = { !it.isBattling },
            quirks = arrayOf(blink1, blink2, blink3),
            idleAnimations = arrayOf(
                singleBoneLook(pitchMultiplier = 1F, yawMultiplier = 0.4F),
                SingleBoneLookAnimation(lefthead, false, false, false, false, 1F, 1.5F, 45F, -45F, 45F, 10F),
                SingleBoneLookAnimation(righthead, false, false, false, false, 1F, 1.5F, 45F, -45F, 10F, -45F),
                bedrock("dodrio", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2, blink3),
            idleAnimations = arrayOf(
                bedrock("dodrio", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2, blink3, bite1, bite2, bite3),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                bedrock("dodrio", "battle_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("dodrio", "faint") else null
}