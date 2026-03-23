/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.mixin;

import com.cobblemon.mod.fabric.brewing.CobblemonFabricBreweryRegistry;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin
{
    @Inject(
            method = "isValid",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void cobblemon$isValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        final BrewingStandBlockEntity entity = (BrewingStandBlockEntity) (Object) this;
        if (slot < 3 && !cir.getReturnValue())
        {
            if (entity.getStack(slot).isEmpty() && CobblemonFabricBreweryRegistry.INSTANCE.isValidPotionSlot(stack))
                cir.setReturnValue(true);
        }
    }
}
