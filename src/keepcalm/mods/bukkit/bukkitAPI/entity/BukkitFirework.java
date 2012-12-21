package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

import net.minecraft.entity.item.EntityFireworkRocket;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;

public class BukkitFirework extends BukkitEntity implements Firework {

	public BukkitFirework(BukkitServer server, EntityFireworkRocket entity) {
		super(server, entity);
		
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

}
