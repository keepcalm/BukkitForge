package org.bukkit.craftbukkit.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityBrewingStand;

import org.bukkit.block.BrewingStand;
import org.bukkit.craftbukkit.block.CraftBrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryBrewer extends CraftInventory implements BrewerInventory {
    public CraftInventoryBrewer(IInventory inventory) {
        super(inventory);
    }

    public ItemStack getIngredient() {
        return getItem(3);
    }

    public void setIngredient(ItemStack ingredient) {
        setItem(3, ingredient);
    }

    @Override
    public BrewingStand getHolder() {
    	TileEntityBrewingStand x = (TileEntityBrewingStand)getInventory();
        return new CraftBrewingStand(x.xCoord, x.yCoord, x.zCoord, x.worldObj);//();
    }
}
