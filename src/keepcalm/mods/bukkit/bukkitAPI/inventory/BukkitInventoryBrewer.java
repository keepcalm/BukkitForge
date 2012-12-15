package keepcalm.mods.bukkit.bukkitAPI.inventory;

import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityBrewingStand;

import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryBrewer extends BukkitInventory implements BrewerInventory {
    public BukkitInventoryBrewer(IInventory inventory) {
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
    	TileEntityBrewingStand x = (TileEntityBrewingStand) inventory;
        return new BukkitBrewingStand(x.xCoord, x.yCoord, x.zCoord, x.worldObj);//();
    }
}
