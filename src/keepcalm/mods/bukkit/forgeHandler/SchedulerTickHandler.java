package keepcalm.mods.bukkit.forgeHandler;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerQuitEvent;

import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.scheduler.B4VScheduler;
import keepcalm.mods.bukkit.utils.CaseInsensitiveArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class SchedulerTickHandler implements ITickHandler {
	public static int tickOffset = 0;
	
	private int progress = 0;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (!ForgeEventHandler.ready) {
			tickOffset++;
			return;
		}
		B4VScheduler b4v = (B4VScheduler) BukkitServer.instance().
				getScheduler();
		if (b4v == null) {
			System.out.println("WARNING: BukkitForge is ready, but scheduler is not set!");
			tickOffset++;
			return;
		}
		// the supposed ticks will always be tickOffset behind the actual number of ticks
		b4v.mainThreadHeartbeat(MinecraftServer.getServer().getTickCounter() - tickOffset);
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (!ForgeEventHandler.ready)return;
		if (progress < 21) {
			progress++;
			return;
		}
		progress = 0;
		CaseInsensitiveArrayList cial = new CaseInsensitiveArrayList(MinecraftServer.getServer().getAllUsernames());
		if (cial.size() == 0) return;
		if (cial.equals(PlayerTracker.online));
		for (String i : PlayerTracker.online) {
			if (!cial.contains(i)) {
				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().createPlayerForUser(i);
				PlayerQuitEvent ev = new PlayerQuitEvent(BukkitPlayerCache.getBukkitPlayer(player), player.username + " left the game");
				Bukkit.getPluginManager().callEvent(ev);
				PlayerTracker.online.remove(i);
			}
			
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.range(TickType.SERVER, TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "BukkitForgeScheduleHelper";
	}

}
