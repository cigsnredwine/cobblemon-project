/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.molang

import com.bedrockk.molang.runtime.MoLangEnvironment
import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.MoParams
import com.bedrockk.molang.runtime.struct.ArrayStruct
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.struct.VariableStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.MoValue
import com.bedrockk.molang.runtime.value.StringValue
import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.dialogue.PlayerDialogueFaceProvider
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunctions
import com.cobblemon.mod.common.util.isInt
import com.cobblemon.mod.common.util.itemRegistry
import net.minecraft.block.Block
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtDouble
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.dimension.DimensionType

/**
 * Holds a bunch of useful MoLang trickery that can be used or extended in API
 *
 * @author Hiroku
 * @since October 2nd, 2023
 */
object MoLangFunctions {
    val generalFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>(
        "is_int" to java.util.function.Function { params -> DoubleValue(params.get<MoValue>(0).asString().isInt()) },
        "is_number" to java.util.function.Function { params -> DoubleValue(params.get<MoValue>(0).asString().toDoubleOrNull() != null) },
        "to_number" to java.util.function.Function { params -> DoubleValue(params.get<MoValue>(0).asString().toDoubleOrNull() ?: 0.0) },
        "to_int" to java.util.function.Function { params -> DoubleValue(params.get<MoValue>(0).asString().toIntOrNull() ?: 0) },
        "to_string" to java.util.function.Function { params -> StringValue(params.get<MoValue>(0).asString()) },
        "do_effect_walks" to java.util.function.Function { _ ->
            DoubleValue(Cobblemon.config.walkingInBattleAnimations)
        },
        "random" to java.util.function.Function { params ->
            val options = mutableListOf<MoValue>()
            var index = 0
            while (params.contains(index)) {
                options.add(params.get(index))
                index++
            }
            return@Function options.random() // Can throw an exception if they specified no args. They'd be idiots though.
        },
        "curve" to java.util.function.Function { params ->
            val curveName = params.getString(0)
            val curve = WaveFunctions.functions[curveName] ?: throw IllegalArgumentException("Unknown curve: $curveName")
            return@Function ObjectValue(curve)
        },
        "array" to java.util.function.Function { params ->
            val values = params.params
            val array = ArrayStruct(hashMapOf())
            values.forEachIndexed { index, moValue -> array.setDirectly("$index", moValue) }
            return@Function array
        }
    )
    val biomeFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val worldFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val dimensionTypeFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val blockFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val playerFunctions = mutableListOf<(ServerPlayerEntity) -> HashMap<String, java.util.function.Function<MoParams, Any>>>(
        { player ->
            hashMapOf(
                "username" to java.util.function.Function { _ -> StringValue(player.gameProfile.name) },
                "uuid" to java.util.function.Function { _ -> StringValue(player.gameProfile.id.toString()) },
                "data" to java.util.function.Function { _ -> return@Function Cobblemon.molangData.load(player.uuid) },
                "save_data" to java.util.function.Function { _ -> Cobblemon.molangData.save(player.uuid) },
                "main_held_item" to java.util.function.Function { _ -> player.world.itemRegistry.getEntry(player.mainHandStack.item).asMoLangValue(RegistryKeys.ITEM) },
                "off_held_item" to java.util.function.Function { _ -> player.world.itemRegistry.getEntry(player.offHandStack.item).asMoLangValue(RegistryKeys.ITEM) },
                "face" to java.util.function.Function { _ -> ObjectValue(PlayerDialogueFaceProvider(player.uuid)) }
            )
        }
    )

    fun RegistryEntry<Biome>.asBiomeMoLangValue() = asMoLangValue(RegistryKeys.BIOME).addFunctions(biomeFunctions)
    fun RegistryEntry<World>.asWorldMoLangValue() = asMoLangValue(RegistryKeys.WORLD).addFunctions(worldFunctions)
    fun RegistryEntry<Block>.asBlockMoLangValue() = asMoLangValue(RegistryKeys.BLOCK).addFunctions(blockFunctions)
    fun RegistryEntry<DimensionType>.asDimensionTypeMoLangValue() = asMoLangValue(RegistryKeys.DIMENSION_TYPE).addFunctions(dimensionTypeFunctions)
    fun ServerPlayerEntity.asMoLangValue(): ObjectValue<ServerPlayerEntity> {
        val value = ObjectValue(
            obj = this,
            stringify = { it.entityName }
        )
        value.addFunctions(playerFunctions.flatMap { it(this).entries.map { it.key to it.value } }.toMap())
        return value
    }

    fun <T> RegistryEntry<T>.asMoLangValue(key: RegistryKey<Registry<T>>): ObjectValue<RegistryEntry<T>> {
        val value = ObjectValue(
            obj = this,
            stringify = { it.key.get().value.toString() }
        )
        value.functions.put("is_in") {
            val tag = TagKey.of(key, Identifier(it.getString(0).replace("#", "")))
            return@put DoubleValue(if (value.obj.isIn(tag)) 1.0 else 0.0)
        }
        value.functions.put("is_of") {
            val identifier = Identifier(it.getString(0))
            return@put DoubleValue(if (value.obj.matchesId(identifier)) 1.0 else 0.0)
        }
        return value
    }

    fun QueryStruct.addStandardFunctions(): QueryStruct {
        functions.putAll(generalFunctions)
        return this
    }

    fun <T : QueryStruct> T.addFunctions(functions: Map<String, java.util.function.Function<MoParams, Any>>): T {
        this.functions.putAll(functions)
        return this
    }

    fun <T : QueryStruct> T.addFunction(name: String, function: java.util.function.Function<MoParams, Any>): T {
        this.functions[name] = function
        return this
    }

    fun MoLangEnvironment.getQueryStruct(name: String = "query") = structs.getOrPut(name) { QueryStruct(hashMapOf()) } as QueryStruct

    fun MoLangRuntime.setup(): MoLangRuntime {
        environment.getQueryStruct().addStandardFunctions()
        return this
    }

    fun writeMoValueToNBT(value: MoValue): NbtElement? {
        return when (value) {
            is DoubleValue -> NbtDouble.of(value.value)
            is StringValue -> NbtString.of(value.value)
            is ArrayStruct -> {
                val list = value.map.values
                val nbtList = NbtList()
                list.mapNotNull(::writeMoValueToNBT).forEach(nbtList::add)
                nbtList
            }
            is VariableStruct -> {
                val nbt = NbtCompound()
                value.map.forEach { (key, value) ->
                    val element = writeMoValueToNBT(value) ?: return@forEach
                    nbt.put(key, element)
                }
                nbt
            }
            else -> null
        }
    }

    fun readMoValueFromNBT(nbt: NbtElement): MoValue {
        return when (nbt) {
            is NbtDouble -> DoubleValue(nbt.doubleValue())
            is NbtString -> StringValue(nbt.asString())
            is NbtList -> {
                val array = ArrayStruct(hashMapOf())
                var index = 0
                nbt.forEach { element ->
                    val value = readMoValueFromNBT(element)
                    array.setDirectly("$index", value)
                    index++
                }
                array
            }
            is NbtCompound -> {
                val variable = VariableStruct(hashMapOf())
                nbt.keys.toList().forEach { key ->
                    val value = readMoValueFromNBT(nbt[key]!!)
                    variable.map[key] = value
                }
                variable
            }
            else -> null
        } ?: throw IllegalArgumentException("Invalid NBT element type: ${nbt.type}")
    }
}