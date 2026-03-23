/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.resource.ResourceFactory
import net.minecraft.util.Identifier

data class ShaderRegistryData(val resourceFactory: ResourceFactory, val shaderName: Identifier, val vertexFormat: VertexFormat)