/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.dialogue.dto

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.text.text
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

class DialogueOptionDTO(
    var text: MutableText = "".text(),
    var value: String = "",
    var selectable: Boolean = true
): Encodable, Decodable {
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeText(text)
        buffer.writeString(value)
        buffer.writeBoolean(selectable)
    }

    override fun decode(buffer: PacketByteBuf) {
        text = buffer.readText().copy()
        value = buffer.readString()
        selectable = buffer.readBoolean()
    }
}