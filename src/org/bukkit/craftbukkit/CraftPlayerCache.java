package org.bukkit.craftbukkit;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.craftbukkit.entity.BukkitPlayer;

public class CraftPlayerCache {

	
	
	private static final HashMap<String, CraftPlayer> playerCache = new HashMap<String, BukkitPlayer>();
	
	public static CraftPlayer getBukkitPlayer(EntityPlayerMP player) {
		if (playerCache.containsKey(player.username))
		{
			BukkitPlayer ply = playerCache.get(player.username);
			if (ply.getHandle().isDead) {
				// new player needed
				//removePlayer(player.username);
				ply.setHandle(player);
				return ply;
			}
			else {
				return ply;
			}
		}
		playerCache.put(player.username, new CraftPlayer(player));
		return playerCache.get(player.username);
	}
	
	public static CraftPlayer getBukkitPlayer( BukkitServer server, EntityPlayerMP player) {
		/*if (playerCache.containsKey(player.username))
			return playerCache.get(player.username);
		playerCache.put(player.username, new CraftPlayer(server, player));
		return playerCache.get(player.username);*/
		return getBukkitPlayer(player);
	}
	
	public static void removePlayer(String name) {
		if (playerCache.containsKey(name))
			playerCache.remove(name);
	}
	
}
