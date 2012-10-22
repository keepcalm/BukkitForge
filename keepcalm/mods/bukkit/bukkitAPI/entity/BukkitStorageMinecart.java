package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventory;
import net.minecraft.src.EntityMinecart;

import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
//import org.bukkit.craftbukkit.inventory.BukkitInventory;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitStorageMinecart extends BukkitMinecart implements StorageMinecart {
    private final BukkitInventory inventory;

    public BukkitStorageMinecart(BukkitServer server, EntityMinecart entity) {
        super(server, entity);
        inventory = new BukkitInventory(entity);
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return "BukkitStorageMinecart{" + "inventory=" + inventory + '}';
    }
}
