package org.bukkit.craftbukkit.v1_5_R2.inventory;

import java.util.Iterator;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class RecipeIterator implements Iterator<Recipe> {
    private Iterator<IRecipe> recipes;
    private Iterator<Integer> smelting;
    private Iterator<?> removeFrom = null;

    @SuppressWarnings("unchecked")
	public RecipeIterator() {
        this.recipes = CraftingManager.getInstance().getRecipeList().iterator();
        this.smelting = FurnaceRecipes.smelting().getSmeltingList().keySet().iterator();
    }

    public boolean hasNext() {
        if (recipes.hasNext()) {
            return true;
        } else {
            return smelting.hasNext();
        }
    }

    public Recipe next() {
        if (recipes.hasNext()) {
            removeFrom = recipes;
            return new BukkitRecipe(recipes.next());
        } else {
            removeFrom = smelting;
            int id = smelting.next();
            CraftItemStack stack = new CraftItemStack(FurnaceRecipes.smelting().getSmeltingResult(id));
            CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(stack, new ItemStack(id, 1, (short) -1));
            return recipe;
        }
    }

    public void remove() {
        if (removeFrom == null) {
            throw new IllegalStateException();
        }
        removeFrom.remove();
    }
}
