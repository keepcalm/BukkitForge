package org.bukkit.craftbukkit;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.craftbukkit.entity.CraftPlayer;

public class CraftPlayerCache {

	
	
	private static final HashMap<String, CraftPlayer> playerCache = new HashMap<String, CraftPlayer>();
	
	public static CraftPlayer getCraftPlayer(EntityPlayerMP player) {
		System.out.println("Looking for " + player.username + " in cache...");
		try {
			throw new Exception("This is some MOAR DEBUG :P");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (playerCache.containsKey(player.username.toLowerCase()))
		{
			//System.out.println("Found them!");
			CraftPlayer ply = playerCache.get(player.username.toLowerCase());
			if (ply.getHandle().isDead) {
				System.out.println("Cached player's handle is dead, updating...");
				// new player needed
				//removePlayer(player.username);
				ply.setHandle(player);
			//	System.out.println("Returning " + ply);
				return ply;
			}
			else {
				//System.out.println("Returning " + ply);
				return ply;
			}
		}
		CraftPlayer ply = new CraftPlayer(player);
		System.out.println("Created a new CraftPlayer: " + ply);
		playerCache.put(player.username.toLowerCase(), ply);
		return ply;
	}
	
	public static CraftPlayer getCraftPlayer( CraftServer server, EntityPlayerMP player) {
		/*if (playerCache.containsKey(player.username))
			return playerCache.get(player.username);
		playerCache.put(player.username, new CraftPlayer(server, player));
		return playerCache.get(player.username);*/
		return getCraftPlayer(player);
	}
	
	public static void removePlayer(String name) {
		System.out.println("RemovePlayer: " + name);
		if (playerCache.containsKey(name))
			playerCache.remove(name);
	}
	
}
