/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.client

import com.cobblemon.mod.common.platform.events.ClientPlayerEvent
import com.cobblemon.mod.common.platform.events.ClientTickEvent
import com.cobblemon.mod.common.platform.events.PlatformEvents
import net.minecraft.client.MinecraftClient
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.eventbus.api.SubscribeEvent


@OnlyIn(Dist.CLIENT)
object ForgeClientPlatformEventHandler {

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onTick(e: TickEvent.ClientTickEvent) {
        if (e.phase == TickEvent.Phase.START) {
            PlatformEvents.CLIENT_TICK_PRE.post(ClientTickEvent.Pre(MinecraftClient.getInstance()))
        }
        else {
            PlatformEvents.CLIENT_TICK_POST.post(ClientTickEvent.Post(MinecraftClient.getInstance()))
        }
    }

    @SubscribeEvent
    fun onLogin(e: ClientPlayerNetworkEvent.LoggingIn) {
        PlatformEvents.CLIENT_PLAYER_LOGIN.post(ClientPlayerEvent.Login(e.player))
    }

    @SubscribeEvent
    fun onLogout(e: ClientPlayerNetworkEvent.LoggingOut) {
        PlatformEvents.CLIENT_PLAYER_LOGOUT.post(ClientPlayerEvent.Logout(e.player ?: return))
    }

    @SubscribeEvent
    fun onItemTooltip(e: ItemTooltipEvent) {
        PlatformEvents.CLIENT_ITEM_TOOLTIP.post(com.cobblemon.mod.common.platform.events.ItemTooltipEvent(e.itemStack, e.flags, e.toolTip))
    }

}