/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.molang

import com.bedrockk.molang.runtime.struct.VariableStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.readMoValueFromNBT
import com.cobblemon.mod.common.api.molang.MoLangFunctions.writeMoValueToNBT
import com.cobblemon.mod.common.platform.events.PlatformEvents
import java.nio.file.Path
import java.util.UUID
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import net.minecraft.util.WorldSavePath

object NbtMoLangDataStoreFactory : MoLangDataStoreFactory {
    lateinit var savePath: Path
    val cache = mutableMapOf<UUID, VariableStruct>()
    val dirty = mutableListOf<UUID>()

    var ticker = 0
    var saveTicks = 20 * 5 // Every 5 seconds. It's really not going to end up being that much dirty data nor take long.

    init {
        PlatformEvents.SERVER_STARTED.subscribe { event -> savePath = event.server.getSavePath(WorldSavePath.PLAYERDATA).parent }
        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe { event ->
            val uuid = event.player.uuid
            if (uuid in dirty) {
                save(uuid)
                cache.remove(uuid)
            }
        }
        PlatformEvents.SERVER_STOPPING.subscribe {
            saveAll()
            cache.clear()
            dirty.clear()
        }
        PlatformEvents.SERVER_TICK_POST.subscribe {
            ticker++
            if (ticker % saveTicks == 0 && dirty.size > 0) {
                saveAll()
            }
        }
    }

    fun saveAll() {
        dirty.toList().forEach(::save)
    }

    override fun markDirty(uuid: UUID) {
        dirty.add(uuid)
    }

    override fun load(uuid: UUID): VariableStruct {
        return if (cache.contains(uuid))
            cache[uuid]!!
        else {
            val file = this.file(uuid)
            if (!file.exists()) {
                val data = VariableStruct()
                cache[uuid] = data
                return data
            }

            val nbt = NbtIo.readCompressed(file(uuid))

            // If it's not a VariableStruct then someone's fucked around and will subsequently find out
            val data = readMoValueFromNBT(nbt) as VariableStruct
            cache[uuid] = data
            data
        }
    }

    fun save(uuid: UUID) {
        val file = file(uuid)
        val data = cache[uuid] ?: return
        val nbt = writeMoValueToNBT(data)!! as NbtCompound
        file.parentFile.mkdirs()
        NbtIo.writeCompressed(nbt, file)
        dirty -= uuid
    }

    private fun file(uuid: UUID) = savePath.resolve("playermolangdata/${uuid.toString().substring(0, 2)}/$uuid.dat").toFile()
}