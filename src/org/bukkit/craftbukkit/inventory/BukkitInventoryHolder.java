package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BukkitInventoryHolder implements InventoryHolder {

	private Inventory inv;
	
	public BukkitInventoryHolder(Inventory inv) {
		this.inv = inv;
	}
	
	@Override
	public Inventory getInventory() {
		return this.inv;
	}

}
