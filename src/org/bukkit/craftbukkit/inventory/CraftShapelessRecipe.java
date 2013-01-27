package org.bukkit.craftbukkit.inventory;

import java.util.List;

import net.minecraft.item.crafting.ShapelessRecipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import cpw.mods.fml.common.registry.GameRegistry;

public class CraftShapelessRecipe extends ShapelessRecipe implements CraftRecipe {
    // TODO: Could eventually use this to add a matches() method or some such
    //private ShapelessRecipes recipe;

    public CraftShapelessRecipe(ItemStack result) {
        super(result);
    }

    public CraftShapelessRecipe(ItemStack result, ShapelessRecipes recipe) {
        this(result);
        //this.recipe = recipe;
    }

    public static CraftShapelessRecipe fromBukkitRecipe(ShapelessRecipe recipe) {
        if (recipe instanceof CraftShapelessRecipe) {
            return (BukkitShapelessRecipe) recipe;
        }
        CraftShapelessRecipe ret = new BukkitShapelessRecipe(recipe.getResult());
        for (ItemStack ingred : recipe.getIngredientList()) {
            ret.addIngredient(ingred.getType(), ingred.getDurability());
        }
        return ret;
    }

    public void addToCraftingManager() {
        List<ItemStack> ingred = this.getIngredientList();
        Object[] data = new Object[ingred.size()];
        int i = 0;
        for (ItemStack mdata : ingred) {
            int id = mdata.getTypeId();
            short dmg = mdata.getDurability();
            data[i] = new net.minecraft.item.ItemStack(id, 1, dmg);
            i++;
        }
        GameRegistry.addShapelessRecipe(BukkitItemStack.createNMSItemStack(this.getResult()), data);
    }
}
