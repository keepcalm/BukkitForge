package org.bukkit.craftbukkit.inventory;

import net.minecraft.inventory.IInventory;

import org.bukkit.inventory.AnvilInventory;

public class BukkitInventoryAnvil extends BukkitInventory implements AnvilInventory {
    public BukkitInventoryAnvil(IInventory anvil) {
        super(anvil);
    }
}
