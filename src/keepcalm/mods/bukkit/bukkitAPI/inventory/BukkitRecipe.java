package keepcalm.mods.bukkit.bukkitAPI.inventory;

import keepcalm.mods.bukkit.bukkitAPI.item.BukkitItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class BukkitRecipe implements Recipe {
	private ItemStack result;
	
	public BukkitRecipe(ShapedRecipes recipe) {
		this.result = new BukkitItemStack(recipe.getRecipeOutput());
		
	}
	public BukkitRecipe(ShapelessRecipes shapeless) {
		this.result = new BukkitItemStack(shapeless.getRecipeOutput());
	}
	
	@Override
	public ItemStack getResult() {
		return result;
	}

}
