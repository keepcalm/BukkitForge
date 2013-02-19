package keepcalm.mods.bukkit.forgeHandler;

import java.util.EnumSet;

import keepcalm.mods.bukkit.utils.CaseInsensitiveArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;
import keepcalm.mods.bukkit.CraftPlayerCache;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.event.player.PlayerQuitEvent;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class SchedulerTickHandler implements ITickHandler {
	public static int tickOffset = 0;

	private int progress = 0;

	private Thread playerCheckerThread;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (!ForgeEventHandler.ready) {
			tickOffset++;
			return;
		}
		CraftScheduler b4v = (CraftScheduler) CraftServer.instance().getScheduler();
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
		if (this.playerCheckerThread != null && this.playerCheckerThread.isAlive()) return;
		if (progress < 21) {
			progress++;
			return;
		}
		if (this.playerCheckerThread != null)
			try {
				playerCheckerThread.join();
			} catch (InterruptedException e) {
				return; // we don't want threads to be left running!
			}
		progress = 0;
		this.playerCheckerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CaseInsensitiveArrayList cial = new CaseInsensitiveArrayList(MinecraftServer.getServer().getAllUsernames());
				if (cial.size() == 0) return;
				if (cial.equals(PlayerTracker.online));
				for (String i : PlayerTracker.online) {
					if (!cial.contains(i)) {
						EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().createPlayerForUser(i);
						PlayerQuitEvent ev = new PlayerQuitEvent(CraftPlayerCache.getCraftPlayer(player), player.username + " left the game");
						Bukkit.getPluginManager().callEvent(ev);
						PlayerTracker.online.remove(i);
					}

				}
				
			}
		});
		playerCheckerThread.start();
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
