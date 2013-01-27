package org.bukkit.craftbukkit.inventory;

import net.minecraft.inventory.IInventory;

import org.bukkit.inventory.AnvilInventory;

public class CraftInventoryAnvil extends BukkitInventory implements AnvilInventory {
    public CraftInventoryAnvil(IInventory anvil) {
        super(anvil);
    }
}
