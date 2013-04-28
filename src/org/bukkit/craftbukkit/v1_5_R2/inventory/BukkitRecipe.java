package org.bukkit.craftbukkit.v1_5_R2.inventory;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class BukkitRecipe implements Recipe {
	private ItemStack result;
	
	public BukkitRecipe(ShapedRecipes recipe) {
		this.result = new CraftItemStack(recipe.getRecipeOutput());
		
	}
	public BukkitRecipe(ShapelessRecipes shapeless) {
		this.result = new CraftItemStack(shapeless.getRecipeOutput());
	}
	
	public BukkitRecipe(IRecipe recipe) {
		this.result = new CraftItemStack(recipe.getRecipeOutput());
	}
	
	@Override
	public ItemStack getResult() {
		return result;
	}

}
