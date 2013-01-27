package org.bukkit.craftbukkit;

import net.minecraft.item.crafting.CraftingManager;

public class BukkitModRecipeHelper {

	private static CraftingManager theCraftingManager;
	
	public static void saveCraftingManagerRecipes() {
		theCraftingManager = CraftingManager.getInstance();
	}
	
	public static CraftingManager getOriginalCraftingManager() {
		if (theCraftingManager == null)
			saveCraftingManagerRecipes();
		return theCraftingManager;
	}
	
}
