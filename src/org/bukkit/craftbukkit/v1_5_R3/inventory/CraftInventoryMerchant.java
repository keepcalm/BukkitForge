package org.bukkit.craftbukkit.v1_5_R3.inventory;

import net.minecraft.inventory.InventoryMerchant;

import org.bukkit.inventory.MerchantInventory;

public class CraftInventoryMerchant extends CraftInventory implements MerchantInventory {
    public CraftInventoryMerchant(InventoryMerchant merchant) {
        super(merchant);
    }
}
