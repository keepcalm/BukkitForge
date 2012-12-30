package keepcalm.mods.bukkit.bukkitAPI.inventory;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class BukkitFurnaceRecipe extends FurnaceRecipe implements CraftRecipe {
    public BukkitFurnaceRecipe(ItemStack result, ItemStack source) {
        super(result, source.getType(), source.getDurability());
    }

    public static BukkitFurnaceRecipe fromBukkitRecipe(FurnaceRecipe recipe) {
        if (recipe instanceof BukkitFurnaceRecipe) {
            return (BukkitFurnaceRecipe) recipe;
        }
        return new BukkitFurnaceRecipe(recipe.getResult(), recipe.getInput());
    }

    public void addToCraftingManager() {
        ItemStack result = this.getResult();
        ItemStack input = this.getInput();
        GameRegistry.addSmelting(input.getTypeId(), BukkitItemStack.createNMSItemStack(result), 0.1f);
    }
}
