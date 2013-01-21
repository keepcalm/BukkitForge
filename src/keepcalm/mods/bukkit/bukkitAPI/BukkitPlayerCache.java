package keepcalm.mods.bukkit.bukkitAPI;

import java.util.HashMap;

import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class BukkitPlayerCache {

	
	
	private static final HashMap<String, BukkitPlayer> playerCache = new HashMap<String, BukkitPlayer>();
	
	public static BukkitPlayer getBukkitPlayer(EntityPlayerMP player) {
		if (playerCache.containsKey(player.username))
			return playerCache.get(player.username);
		playerCache.put(player.username, new BukkitPlayer(player));
		return playerCache.get(player.username);
	}
	
	public static BukkitPlayer getBukkitPlayer( BukkitServer server, EntityPlayerMP player) {
		if (playerCache.containsKey(player.username))
			return playerCache.get(player.username);
		playerCache.put(player.username, new BukkitPlayer(server, player));
		return playerCache.get(player.username);
	}
	
	public static void removePlayer(String name) {
		if (playerCache.containsKey(name))
			playerCache.remove(name);
	}
	
}
