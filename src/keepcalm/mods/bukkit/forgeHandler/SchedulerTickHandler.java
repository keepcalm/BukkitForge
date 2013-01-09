package keepcalm.mods.bukkit.forgeHandler;

import java.util.EnumSet;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.scheduler.B4VScheduler;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class SchedulerTickHandler implements ITickHandler {
	public static int tickOffset = 0;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (!ForgeEventHandler.ready) {
			tickOffset++;
			return;
		}
		B4VScheduler b4v = (B4VScheduler) BukkitServer.instance().
				getScheduler();
		// the supposed ticks will always be tickOffset behind the actual number of ticks
		b4v.mainThreadHeartbeat(MinecraftServer.getServer().getTickCounter() - tickOffset);
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

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
