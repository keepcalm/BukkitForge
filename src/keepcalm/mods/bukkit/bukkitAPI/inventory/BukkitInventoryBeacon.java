package keepcalm.mods.bukkit.bukkitAPI.inventory;

import net.minecraft.tileentity.TileEntityBeacon;

import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryBeacon extends BukkitInventory implements BeaconInventory {
    public BukkitInventoryBeacon(TileEntityBeacon beacon) {
        super(beacon);
    }

    public void setItem(ItemStack item) {
        setItem(0, item);
    }

    public ItemStack getItem() {
        return getItem(0);
    }
}
