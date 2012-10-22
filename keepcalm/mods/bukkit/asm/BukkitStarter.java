package keepcalm.mods.bukkit.asm;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CrashReport;
import net.minecraft.src.MinecraftException;

public class BukkitStarter implements Runnable {

	@Override
	public void run() {
		try {
			BukkitContainer.bServer = new BukkitServer(MinecraftServer.getServer(), MinecraftServer.getServer().getConfigurationManager());
		}
		catch (Exception e) {
			e.printStackTrace();
			//MinecraftException ex = new MinecraftException("Failed to launch the Bukkit API!");
			CrashReport c = new CrashReport("The mod Bukkit4Vanilla failed to launch", e);
			c.addCrashSection("Bukkit API Details", "Error encountered: " + e.getStackTrace());
			c.saveToFile(c.getFile());
			MinecraftServer.logger.severe(c.getCompleteReport());
			MinecraftServer.logger.severe("You game will now be stopped. Please report this to the mod developer.");
			
//			MinecraftServer.getServer().stopServer();
		}

	}

}
