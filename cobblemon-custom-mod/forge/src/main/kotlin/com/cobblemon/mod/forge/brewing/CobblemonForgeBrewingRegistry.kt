/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.brewing

import com.cobblemon.mod.common.brewing.BrewingRecipes
import com.cobblemon.mod.common.brewing.ingredient.CobblemonIngredient
import com.cobblemon.mod.common.brewing.ingredient.CobblemonItemIngredient
import com.cobblemon.mod.common.brewing.ingredient.CobblemonPotionIngredient
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.potion.Potions
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Identifier
import net.minecraftforge.common.brewing.BrewingRecipe
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.common.crafting.AbstractIngredient
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.IIngredientSerializer
import net.minecraftforge.registries.ForgeRegistries

internal object CobblemonForgeBrewingRegistry {

    fun register() {
        this.registerIngredientTypes()
        this.registerRecipes()
    }

    private fun registerIngredientTypes() {
        CraftingHelper.register(cobblemonResource("potion"), ForgePotionIngredientSerializer)
    }

    private fun registerRecipes() {
        BrewingRecipes.recipes.forEach { (input, ingredient, output) ->
            BrewingRecipeRegistry.addRecipe(BrewingRecipe(this.wrapIngredient(input), this.wrapIngredient(ingredient), output.defaultStack))
        }
    }

    private fun wrapIngredient(ingredient: CobblemonIngredient): Ingredient = when (ingredient) {
        is CobblemonItemIngredient -> Ingredient.ofItems(ingredient.item)
        is CobblemonPotionIngredient -> ForgePotionIngredient(ingredient)
    }

    private class ForgePotionIngredient(val base: CobblemonPotionIngredient) : AbstractIngredient() {

        override fun toJson(): JsonElement {
            val json = JsonObject()
            json.addProperty("potion", ForgeRegistries.POTIONS.getKey(this.base.potion)!!.toString())
            return json
        }

        override fun isSimple(): Boolean = false

        override fun getSerializer(): IIngredientSerializer<out Ingredient> = ForgePotionIngredientSerializer

        override fun test(arg: ItemStack?): Boolean = arg != null && this.base.matches(arg)

        override fun getMatchingStacks(): Array<ItemStack> = this.base.matchingStacks().toTypedArray()

    }

    private object ForgePotionIngredientSerializer : IIngredientSerializer<ForgePotionIngredient> {

        override fun parse(buf: PacketByteBuf): ForgePotionIngredient {
            val id = buf.readIdentifier()
            val potion = ForgeRegistries.POTIONS.getValue(id) ?: Potions.EMPTY
            return ForgePotionIngredient(CobblemonPotionIngredient(potion))
        }

        override fun parse(jsonObject: JsonObject): ForgePotionIngredient {
            val id = Identifier(jsonObject.asString)
            val potion = ForgeRegistries.POTIONS.getValue(id) ?: Potions.EMPTY
            return ForgePotionIngredient(CobblemonPotionIngredient(potion))
        }

        override fun write(buf: PacketByteBuf, ingredient: ForgePotionIngredient) {
            buf.writeIdentifier(ForgeRegistries.POTIONS.getKey(ingredient.base.potion)!!)
        }

    }

}