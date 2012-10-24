package keepcalm.mods.bukkit.bukkitAPI.inventory;

import keepcalm.mods.bukkit.bukkitAPI.block.BukkitFurnace;

import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.src.TileEntityFurnace;

public class BukkitInventoryFurnace extends BukkitInventory implements FurnaceInventory {
	private Furnace owner;
    public BukkitInventoryFurnace(TileEntityFurnace inventory) {
        super(inventory);
        owner = new BukkitFurnace(inventory.xCoord, inventory.yCoord, inventory.zCoord, inventory.worldObj);
    }

    public BukkitInventoryFurnace(TileEntityFurnace furnace,
			BukkitFurnace bukkitFurnace) {
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
