/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin  {
    @Inject(method = "equipToOffHand", at = @At(value = "HEAD"), cancellable = true)
    public void cobblemon$isValidBarteringItem(ItemStack stack, CallbackInfo ci) {
        PiglinEntity entity = (PiglinEntity)(Object) this;
        if (stack.isOf(CobblemonItems.RELIC_COIN_POUCH)) {
            entity.equipStack(EquipmentSlot.OFFHAND, stack);
            entity.updateDropChances(EquipmentSlot.OFFHAND);
            ci.cancel();
        }
    }
}
