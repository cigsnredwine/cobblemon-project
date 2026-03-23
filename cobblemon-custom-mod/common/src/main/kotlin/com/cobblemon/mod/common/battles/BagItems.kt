/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.PrioritizedList
import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.battles.runner.ShowdownService
import com.cobblemon.mod.common.item.battle.BagItem
import com.cobblemon.mod.common.item.battle.BagItemConvertible
import com.cobblemon.mod.common.util.cobblemonResource
import java.io.File
import net.minecraft.item.ItemStack
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * A registry for [BagItem]s that could be parsed from [ItemStack]s. This registry is used as the resource loading
 * mechanism for bag item scripts, but add to [bagItems] to map from stacks to [BagItem]s.
 *
 * @author Hiroku
 * @since June 26th, 2023
 */
object BagItems : DataRegistry {
    override val id = cobblemonResource("bag_items")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<BagItems>()
    override fun sync(player: ServerPlayerEntity) {}

    val bagItems = PrioritizedList<BagItemConvertible>()
    internal val bagItemsScripts = mutableMapOf<String, String>() // itemId to JavaScript

    init {
        observable.subscribe {
            Cobblemon.showdownThread.queue(ShowdownService::registerBagItems)
        }
    }

    fun getConvertibleForStack(stack: ItemStack): BagItemConvertible? {
        return bagItems.firstOrNull { it.getBagItem(stack) != null }
    }

    override fun reload(manager: ResourceManager) {
        manager.findResources("bag_items") { it.path.endsWith(".js") }.forEach { (identifier, resource) ->
            resource.inputStream.use { stream ->
                stream.bufferedReader().use { reader ->
                    val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                    val js = reader.readText()
                    bagItemsScripts[resolvedIdentifier.path] = js
                }
            }
        }
        this.observable.emit(this)
    }
}