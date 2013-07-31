package keepcalm.mods.bukkit.forgeHandler;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
import keepcalm.mods.bukkit.utils.CaseInsensitiveArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
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

		online.add(player.username);

		if (DEBUG) System.out.println("User logged in: " + player.username.toLowerCase());

		BukkitContainer.users.put(player.username.toLowerCase(), "SeenBefore");
		if (CraftServer.instance() == null) {
			return;
		}
		
		// process in CraftServer
		Runnable run = new Runnable() {
			public void run() {
				String msg = player.username + " joined the game";
				if (!ForgeEventHandler.ready)
					msg = ""; // nothing - SSP 
				PlayerJoinEvent ev = new PlayerJoinEvent(BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) player), msg);
				Bukkit.getPluginManager().callEvent(ev);
			}
		};

		run.run();



	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		online.remove(player.username);
		PlayerQuitEvent ev = new PlayerQuitEvent(BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) player), player.username + " left the game");
		Bukkit.getPluginManager().callEvent(ev);
		BukkitForgePlayerCache.removePlayer(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		EntityPlayerMP dude = (EntityPlayerMP) player;
		PlayerChangedWorldEvent c = new PlayerChangedWorldEvent(BukkitForgePlayerCache.getCraftPlayer(dude), CraftServer.instance().getWorld(dude.worldObj.provider.dimensionId));
		Bukkit.getPluginManager().callEvent(c);
	}

	@Override
	public void onPlayerRespawn(final EntityPlayer player) {
		if (DEBUG) System.out.println("Player respawned: " + player.username);
		Runnable run = new Runnable() {
			@Override
			public void run() {
				ChunkCoordinates j = player.getBedLocation();
				PlayerRespawnEvent c = new PlayerRespawnEvent(BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) player), new Location(CraftServer.instance().getWorld(player.worldObj.provider.dimensionId), j.posX, j.posY, j.posZ), true);
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
