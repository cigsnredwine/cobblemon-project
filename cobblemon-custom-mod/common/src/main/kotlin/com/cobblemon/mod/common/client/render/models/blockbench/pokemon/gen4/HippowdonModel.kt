/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4

import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.isStandingOnRedSand
import com.cobblemon.mod.common.util.isStandingOnSand
import com.cobblemon.mod.common.util.isStandingOnSandOrRedSand
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class HippowdonModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("hippowdon")
    override val head = getPart("head")

    private val sand = getPart("sand")
    private val redsand = getPart("redsand")

    override var portraitScale = 0.6F
    override var portraitTranslation = Vec3d(-0.63, 0.73, 0.0)

    override var profileScale = 0.4F
    override var profileTranslation = Vec3d(-0.1, 1.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var battleidle: PokemonPose
    lateinit var standingsand: PokemonPose
    lateinit var walksand: PokemonPose
    lateinit var battleidlesand: PokemonPose
    lateinit var battleidleredsand: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var sleepsand: PokemonPose
    lateinit var sleepredsand: PokemonPose
    lateinit var standingredsand: PokemonPose
    lateinit var walkredsand: PokemonPose

    override val cryAnimation = CryProvider { _, pose ->
        when {
            pose.isPosedIn(standingsand, walksand) -> bedrockStateful("hippowdon", "sand_cry")
            pose.isPosedIn(battleidle) -> bedrockStateful("hippowdon", "battle_cry")
            pose.isPosedIn(battleidlesand, battleidleredsand) -> bedrockStateful("hippowdon", "sand_battle_cry")
            else -> bedrockStateful("hippowdon", "cry")
        }
    }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("hippowdon", "blink") }
        val idlequirk = quirk { bedrockStateful("hippowdon", "quirk_idle") }

        sleep = registerPose(
            poseName = "sleep",
            poseType = PoseType.SLEEP,
            condition = { !it.isStandingOnSand() },
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "sleep")
            )
        )

        sleepredsand = registerPose(
            poseName = "sleepsand",
            poseType = PoseType.SLEEP,
            condition = { it.isStandingOnRedSand() },
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "sand_sleep")
            )
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES,
            condition = { !it.isBattling && !it.isStandingOnSandOrRedSand() },
            quirks = arrayOf(blink, idlequirk),
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            condition = { !it.isStandingOnSandOrRedSand() },
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "ground_walk")
            )
        )

        standingsand = registerPose(
            poseName = "standingsand",
            poseTypes = PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink, idlequirk),
            condition = { !it.isBattling && it.isStandingOnSand() },
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "sand_idle")
            )
        )

        standingredsand = registerPose(
            poseName = "standingredsand",
            poseTypes = PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink, idlequirk),
            condition = { !it.isBattling && it.isStandingOnRedSand() },
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "sand_idle")
            )
        )

        walksand = registerPose(
            poseName = "walksand",
            poseTypes = PoseType.MOVING_POSES,
            condition = { it.isStandingOnSand() },
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                bedrock("hippowdon", "sand_swim")
            )
        )

        walkredsand = registerPose(
            poseName = "walkredsand",
            poseTypes = PoseType.MOVING_POSES,
            condition = { it.isStandingOnRedSand() },
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                bedrock("hippowdon", "sand_swim")
            )
        )

        battleidle = registerPose(
            poseName = "battleidle",
            poseTypes = PoseType.STATIONARY_POSES,
            condition = { it.isBattling && !it.isStandingOnSandOrRedSand() },
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "battle_idle")
            )
        )

        battleidlesand = registerPose(
            poseName = "battleidlesand",
            poseTypes = PoseType.STATIONARY_POSES,
            condition = { it.isStandingOnSand() && it.isBattling },
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = true),
                redsand.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "sand_battle_idle")
            )
        )

        battleidleredsand = registerPose(
            poseName = "battleidleredsand",
            poseTypes = PoseType.STATIONARY_POSES,
            condition = { it.isStandingOnRedSand() && it.isBattling },
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                sand.createTransformation().withVisibility(visibility = false),
                redsand.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("hippowdon", "sand_battle_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("hippowdon", "faint") else null
}