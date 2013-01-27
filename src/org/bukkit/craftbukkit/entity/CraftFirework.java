package org.bukkit.craftbukkit.entity;

import java.util.Random;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.bukkit.Material;
import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.craftbukkit.inventory.BukkitItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class CraftFirework extends BukkitEntity implements Firework {
	private final CraftItemStack item;
	
	public CraftFirework(BukkitServer server, EntityFireworkRocket entity) {
		super(server, entity);
		ItemStack item = getHandle().getDataWatcher().getWatchableObjectItemStack(8);
		
		if (item == null) {
			item = new ItemStack(Item.firework);
			getHandle().getDataWatcher().updateObject(8, item);
		}
		this.item = new CraftItemStack(item);
		
		if (this.item.getType() != Material.FIREWORK) {
			this.item.setType(Material.FIREWORK);
		}
	}
	
	@Override
	public EntityFireworkRocket getHandle() {
		return (EntityFireworkRocket) entity;
	}
	
	@Override
	public String toString() {
		return "BukkitFirework{vanillaFirework=" + getHandle() + "}";
	}

	@Override
	public EntityType getType() {
		return EntityType.FIREWORK;
	}

	@Override
	public FireworkMeta getFireworkMeta() {
		return (FireworkMeta) item.getItemMeta();
	}

	@Override
	public void setFireworkMeta(FireworkMeta meta) {
		item.setItemMeta(meta);
		Random rand = new Random();
		getHandle().lifetime = 10 * (1 + meta.getPower() + rand.nextInt(6) + rand.nextInt(7));
		getHandle().getDataWatcher().updateObject(8, item.getHandle());
	}

}
