package org.bukkit.craftbukkit.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;

import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;



public class CraftInventoryEnchanting extends CraftInventory implements EnchantingInventory {
    public CraftInventoryEnchanting(IInventory inventory) {
        super((IInventory) inventory);
    }

    public void setItem(ItemStack item) {
        setItem(0,item);
    }

    public ItemStack getItem() {
        return getItem(0);
    }

    @Override
    public InventoryBasic getInventory() {
        return (InventoryBasic)getInventory();
    }
}
