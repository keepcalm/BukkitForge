package org.bukkit.craftbukkit.v1_5_R2.inventory;

import net.minecraft.inventory.IInventory;

import org.bukkit.inventory.AnvilInventory;

public class CraftInventoryAnvil extends CraftInventory implements AnvilInventory {
    public CraftInventoryAnvil(IInventory anvil) {
        super(anvil);
    }
}
