/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.brewing.ingredient

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtil
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class CobblemonPotionIngredient(val potion: Potion) : CobblemonIngredient {

    override fun matches(stack: ItemStack): Boolean {
        val potion = PotionUtil.getPotion(stack)
        return potion == this.potion
    }

    override fun matchingStacks(): List<ItemStack> {
        val list = arrayListOf<ItemStack>()
        list += PotionUtil.setPotion(Items.POTION.defaultStack, this.potion)
        list += PotionUtil.setPotion(Items.SPLASH_POTION.defaultStack, this.potion)
        list += PotionUtil.setPotion(Items.LINGERING_POTION.defaultStack, this.potion)
        return list
    }

}