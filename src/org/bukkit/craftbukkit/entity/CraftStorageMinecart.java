package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityMinecart;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
//import org.bukkit.craftbukkit.inventory.CraftInventory;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftStorageMinecart extends CraftMinecart implements StorageMinecart {
    private final CraftInventory inventory;

    public CraftStorageMinecart(CraftServer server, EntityMinecart entity) {
        super(server, entity);
        inventory = new CraftInventory(entity);
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return "CraftStorageMinecart{" + "inventory=" + inventory + '}';
    }
}
