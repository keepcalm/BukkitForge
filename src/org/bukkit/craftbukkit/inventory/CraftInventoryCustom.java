package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
//import org.bukkit.craftbukkit.entity.CraftHumanEntity;

public class CraftInventoryCustom extends CraftInventory {
	private InventoryHolder inv;
	
    public CraftInventoryCustom(final InventoryHolder owner, InventoryType type) {
        super(new InventoryBasic(type.getDefaultTitle(), type.getDefaultSize()) { public InventoryHolder getOwner() {return owner;}});
    }

    public CraftInventoryCustom(final InventoryHolder owner, int size) {
        super(new InventoryBasic("Chest", size) { public InventoryHolder getOwner() {return owner;}});
    }

    public CraftInventoryCustom(final InventoryHolder owner, int size, String title) {
        super(new InventoryBasic(title, size) { public InventoryHolder getOwner() {return owner;}});
    }

}