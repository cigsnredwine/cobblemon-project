/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.keybind.keybinds

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinding
import com.cobblemon.mod.common.client.keybind.KeybindCategories
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text

object DebugKeybindings {
    val keybindings = listOf(
        ScaleUpKeybinding(),
        ScaleDownKeybinding(),
        TranslateLeftKeybinding(),
        TranslateRightKeybinding(),
        TranslateUpKeybinding(),
        TranslateDownKeybinding(),
        PrintModelSettingsKeybinding()
    )
    class ScaleUpKeybinding(): CobblemonKeyBinding(
        "key.cobblemon.scaleportraitup",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_EQUAL,
        KeybindCategories.COBBLEMON_DEBUG_CATEGORY
    ) {
        override fun onPress() {
            val currentlySelectedPokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (currentlySelectedPokemon != null) {
                val model = PokemonModelRepository.getPoser(currentlySelectedPokemon.species.resourceIdentifier, currentlySelectedPokemon.aspects)
                model.portraitScale += 0.01F
            }

        }
    }
    class ScaleDownKeybinding(): CobblemonKeyBinding(
        "key.cobblemon.scaleportraitdown",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_MINUS,
        KeybindCategories.COBBLEMON_DEBUG_CATEGORY
    ) {
        override fun onPress() {
            val currentlySelectedPokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (currentlySelectedPokemon != null) {
                val model = PokemonModelRepository.getPoser(currentlySelectedPokemon.species.resourceIdentifier, currentlySelectedPokemon.aspects)
                model.portraitScale -= 0.01F
            }

        }
    }

    class TranslateUpKeybinding: CobblemonKeyBinding(
        "key.cobblemon.translateportraitup",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_I,
        KeybindCategories.COBBLEMON_DEBUG_CATEGORY
    ) {
        override fun onPress() {
            val currentlySelectedPokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (currentlySelectedPokemon != null) {
                val model = PokemonModelRepository.getPoser(currentlySelectedPokemon.species.resourceIdentifier, currentlySelectedPokemon.aspects)
                model.portraitTranslation = model.portraitTranslation.add(0.0, -0.01, 0.0)
            }

        }
    }
    class TranslateDownKeybinding(): CobblemonKeyBinding(
        "key.cobblemon.translateportraitdown",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_K,
        KeybindCategories.COBBLEMON_DEBUG_CATEGORY
    ) {
        override fun onPress() {
            val currentlySelectedPokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (currentlySelectedPokemon != null) {
                val model = PokemonModelRepository.getPoser(currentlySelectedPokemon.species.resourceIdentifier, currentlySelectedPokemon.aspects)
                model.portraitTranslation = model.portraitTranslation.add(0.0, 0.01, 0.0)
            }

        }
    }

    class TranslateLeftKeybinding(): CobblemonKeyBinding(
        "key.cobblemon.translateportraitleft",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_J,
        KeybindCategories.COBBLEMON_DEBUG_CATEGORY
    ) {
        override fun onPress() {
            val currentlySelectedPokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (currentlySelectedPokemon != null) {
                val model = PokemonModelRepository.getPoser(currentlySelectedPokemon.species.resourceIdentifier, currentlySelectedPokemon.aspects)
                model.portraitTranslation = model.portraitTranslation.add(-0.01, 0.0, 0.0)
            }

        }
    }

    class TranslateRightKeybinding(): CobblemonKeyBinding(
        "key.cobblemon.translateportraitright",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_L,
        KeybindCategories.COBBLEMON_DEBUG_CATEGORY
    ) {
        override fun onPress() {
            val currentlySelectedPokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (currentlySelectedPokemon != null) {
                val model = PokemonModelRepository.getPoser(currentlySelectedPokemon.species.resourceIdentifier, currentlySelectedPokemon.aspects)
                model.portraitTranslation = model.portraitTranslation.add(0.01, 0.0, 0.0)
            }
        }
    }

    class PrintModelSettingsKeybinding() : CobblemonKeyBinding(
        "key.cobblemon.printmodelsettings",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_PERIOD,
        KeybindCategories.COBBLEMON_DEBUG_CATEGORY
    ) {
        override fun onPress() {
            val currentlySelectedPokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (currentlySelectedPokemon != null) {
                val model = PokemonModelRepository.getPoser(currentlySelectedPokemon.species.resourceIdentifier, currentlySelectedPokemon.aspects)
                MinecraftClient.getInstance().player?.sendMessage(Text.of("Portrait Translation: ${model.portraitTranslation}"))
                MinecraftClient.getInstance().player?.sendMessage(Text.of("Portrait Scale: ${model.portraitScale}"))
                Cobblemon.LOGGER.info("override var portraitTranslation = Vec3d(${model.portraitTranslation.x}, ${model.portraitTranslation.y}, ${model.portraitTranslation.z})")
                Cobblemon.LOGGER.info("override var portraitScale = ${model.portraitScale}F")
            }
        }
    }
}