package org.bukkit.craftbukkit.v1_5_R3.inventory;

import net.minecraft.tileentity.TileEntityFurnace;

import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_5_R3.block.CraftFurnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryFurnace extends CraftInventory implements FurnaceInventory {
	private Furnace owner;
    public CraftInventoryFurnace(TileEntityFurnace inventory) {
        super(inventory);
        owner = new CraftFurnace(inventory.xCoord, inventory.yCoord, inventory.zCoord, inventory.worldObj);
    }

    public CraftInventoryFurnace(TileEntityFurnace furnace,
			CraftFurnace bukkitFurnace) {
		super(furnace);
		owner = bukkitFurnace;
		
	}

	public ItemStack getResult() {
        return getItem(2);
    }

    public ItemStack getFuel() {
        return getItem(1);
    }

    public ItemStack getSmelting() {
        return getItem(0);
    }

    public void setFuel(ItemStack stack) {
        setItem(1,stack);
    }

    public void setResult(ItemStack stack) {
        setItem(2,stack);
    }

    public void setSmelting(ItemStack stack) {
        setItem(0,stack);
    }

    @Override
    public Furnace getHolder() {
    	return owner;
    }
}
