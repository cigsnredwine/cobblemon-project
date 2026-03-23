/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.dialogue

import com.cobblemon.mod.common.api.molang.ExpressionLike

/**
 * A dialogue that could be sent to players.
 *
 * @author Hiroku
 * @since December 27th, 2023
 */
class Dialogue(
    val pages: List<DialoguePage> = mutableListOf(),
    val escapeAction: DialogueAction = FunctionDialogueAction { dialogue, _ -> dialogue.close() },
    val speakers: Map<String, DialogueSpeaker> = emptyMap()
) {
    companion object {
        fun of(
            pages: Iterable<DialoguePage>,
            escapeAction: ExpressionLike,
            speakers: Map<String, DialogueSpeaker>
        ): Dialogue {
            return Dialogue(
                pages = pages.toList(),
                escapeAction = ExpressionLikeDialogueAction(escapeAction),
                speakers = speakers
            )
        }

        fun of(
            pages: Iterable<DialoguePage>,
            escapeAction: (ActiveDialogue) -> Unit,
            speakers: Map<String, DialogueSpeaker>
        ): Dialogue {
            val dialogue = Dialogue(
                pages = pages.toList(),
                escapeAction = FunctionDialogueAction { activeDialogue, _ -> escapeAction(activeDialogue) },
                speakers = speakers
            )
            return dialogue
        }
    }
}