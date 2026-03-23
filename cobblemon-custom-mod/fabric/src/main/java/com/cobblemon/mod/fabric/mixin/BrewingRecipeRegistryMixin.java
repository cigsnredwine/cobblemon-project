/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.mixin;

import com.cobblemon.mod.fabric.brewing.CobblemonFabricBreweryRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "isValidIngredient", at = @At("RETURN"), cancellable = true)
    private static void cobblemon$isValidIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }
        cir.setReturnValue(CobblemonFabricBreweryRegistry.INSTANCE.isValidIngredientSlot(stack));
    }

    @Inject(method = "hasRecipe", at = @At(value = "HEAD"), cancellable = true)
    private static void cobblemon$hasRecipe(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (CobblemonFabricBreweryRegistry.INSTANCE.hasRecipe(input, ingredient)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "craft", at = @At("RETURN"), cancellable = true)
    private static void cobblemon$craft(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty()) {
            final ItemStack result = CobblemonFabricBreweryRegistry.INSTANCE.recipeResultOf(input, ingredient);
            if (!result.isEmpty()) {
                cir.setReturnValue(result);
            }
        }
    }


}
