package keepcalm.mods.bukkit.forgeHandler;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.utils.CaseInsensitiveArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftPlayerCache;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import cpw.mods.fml.common.IPlayerTracker;

import static keepcalm.mods.bukkit.BukkitContainer.DEBUG;

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
		
		// process in CraftServer
		Runnable run = new Runnable() {
			public void run() {
				String msg = player.username + " joined the game";
				if (!ForgeEventHandler.ready)
					msg = ""; // nothing - SSP 
				PlayerJoinEvent ev = new PlayerJoinEvent(CraftPlayerCache.getCraftPlayer((EntityPlayerMP) player), msg);
				Bukkit.getPluginManager().callEvent(ev);
			}
		};

		run.run();



	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		online.remove(player.username);
		PlayerQuitEvent ev = new PlayerQuitEvent(CraftPlayerCache.getCraftPlayer((EntityPlayerMP) player), player.username + " left the game");
		Bukkit.getPluginManager().callEvent(ev);
		CraftPlayerCache.removePlayer(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		EntityPlayerMP dude = (EntityPlayerMP) player;
		PlayerChangedWorldEvent c = new PlayerChangedWorldEvent(CraftPlayerCache.getCraftPlayer(dude), CraftServer.instance().getWorld(dude.worldObj.getWorldInfo().getDimension()));
		Bukkit.getPluginManager().callEvent(c);
	}

	@Override
	public void onPlayerRespawn(final EntityPlayer player) {
		if (DEBUG) System.out.println("Player respawned: " + player.username);
		Runnable run = new Runnable() {
			@Override
			public void run() {
				ChunkCoordinates j = player.getHomePosition();
				PlayerRespawnEvent c = new PlayerRespawnEvent(CraftPlayerCache.getCraftPlayer((EntityPlayerMP) player), new Location(CraftServer.instance().getWorld(player.worldObj.getWorldInfo().getDimension()), j.posX, j.posY, j.posZ), player.hasHome());
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
