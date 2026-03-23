/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin.invoker;

import net.minecraft.block.*;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unused")
@Mixin(Blocks.class)
public interface BlocksInvoker {

    @Invoker("createLogBlock")
    static PillarBlock createLogBlock(MapColor topMapColor, MapColor sideMapColor) {
        throw new UnsupportedOperationException();
    }

    @Invoker("createLeavesBlock")
    static LeavesBlock createLeavesBlock(BlockSoundGroup soundGroup) {
        throw new UnsupportedOperationException();
    }

    @Invoker("createWoodenButtonBlock")
    static ButtonBlock createWoodenButtonBlock(BlockSetType blockSetType, FeatureFlag... requiredFeatures) {
        throw new UnsupportedOperationException();
    }

    @Invoker("createFlowerPotBlock")
    static FlowerPotBlock createFlowerPotBlock(Block flower, FeatureFlag... requiredFeatures) {
        throw new UnsupportedOperationException();
    }

}
