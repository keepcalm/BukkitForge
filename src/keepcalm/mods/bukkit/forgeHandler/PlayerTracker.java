package keepcalm.mods.bukkit.forgeHandler;

import static keepcalm.mods.bukkit.BukkitContainer.DEBUG;

import java.util.List;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.utils.CaseInsensitiveArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker {

	public static CaseInsensitiveArrayList online = new CaseInsensitiveArrayList();
	
	@Override
	public void onPlayerLogin(final EntityPlayer player) {
		// seen - nvm!
		online.add(player.username);
		if (DEBUG)
		System.out.println("User logged in: " + player.username.toLowerCase());
		BukkitContainer.users.put(player.username.toLowerCase(), "SeenBefore");
		if (!ForgeEventHandler.ready) {
			return;
		}
		// process in BukkitServer
		Runnable run = new Runnable() {
			public void run() {
				String msg = player.username + " joined the game";
				if (!ForgeEventHandler.ready)
					msg = ""; // nothing - SSP 
				PlayerJoinEvent ev = new PlayerJoinEvent(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) player), msg);
				Bukkit.getPluginManager().callEvent(ev);
			}
		};

		Thread x = new Thread(run);
		x.start();




	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		online.remove(player.username);
		PlayerQuitEvent ev = new PlayerQuitEvent(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) player), player.username + " left the game");
		Bukkit.getPluginManager().callEvent(ev);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		EntityPlayerMP dude = (EntityPlayerMP) player;
		PlayerChangedWorldEvent c = new PlayerChangedWorldEvent(BukkitPlayerCache.getBukkitPlayer(dude), BukkitServer.instance().getWorld(dude.worldObj.getWorldInfo().getDimension()));
		Bukkit.getPluginManager().callEvent(c);
	}

	@Override
	public void onPlayerRespawn(final EntityPlayer player) {
		if (DEBUG) System.out.println("Player respawned: " + player.username);
		Runnable run = new Runnable() {
			@Override
			public void run() {
				ChunkCoordinates j = player.getHomePosition();
				PlayerRespawnEvent c = new PlayerRespawnEvent(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) player), new Location(BukkitServer.instance().getWorld(player.worldObj.getWorldInfo().getDimension()), j.posX, j.posY, j.posZ), player.hasHome());
				Bukkit.getPluginManager().callEvent(c);
			}
		};

		if (ForgeEventHandler.ready) {
			run.run();
		}
		else {
			Thread t = new Thread(run);
			t.start();
		}

	}

}
