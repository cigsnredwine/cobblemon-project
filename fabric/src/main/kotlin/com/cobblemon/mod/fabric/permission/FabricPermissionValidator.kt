/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.permission

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.permission.Permission
import com.cobblemon.mod.common.api.permission.PermissionValidator
import net.minecraft.command.CommandSource
import net.minecraft.server.network.ServerPlayerEntity

class FabricPermissionValidator : PermissionValidator {
    private val permissionsClass by lazy {
        Class.forName("me.lucko.fabric.api.permissions.v0.Permissions")
    }

    private val playerCheckMethod by lazy {
        permissionsClass.getMethod("check", ServerPlayerEntity::class.java, String::class.java, Int::class.javaPrimitiveType)
    }

    private val sourceCheckMethod by lazy {
        permissionsClass.getMethod("check", CommandSource::class.java, String::class.java, Int::class.javaPrimitiveType)
    }

    override fun initialize() {
        Cobblemon.LOGGER.info("Booting FabricPermissionValidator, permissions will be checked using fabric-permissions-api, see https://github.com/lucko/fabric-permissions-api")
    }

    override fun hasPermission(player: ServerPlayerEntity, permission: Permission): Boolean {
        return playerCheckMethod.invoke(null, player, permission.literal, permission.level.numericalValue) as Boolean
    }

    override fun hasPermission(source: CommandSource, permission: Permission): Boolean {
        return sourceCheckMethod.invoke(null, source, permission.literal, permission.level.numericalValue) as Boolean
    }
}
