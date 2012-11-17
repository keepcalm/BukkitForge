package keepcalm.mods.bukkit.asm;

import java.util.logging.Logger;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CrashReport;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class BukkitStarter implements Runnable {

	@Override
	public void run() {
		if (MinecraftServer.getServer().getGuiEnabled()) {
			
			
			/*BukkitLoggingStream myLog = new BukkitLoggingStream(MinecraftServer.logger);
			Logger out = Logger.getLogger("STDOUT");//
			out.setParent(MinecraftServer.logger);
			Logger err = Logger.getLogger("STDERR");
			err.setParent(MinecraftServer.logger);
			err.setUseParentHandlers(true);
			out.setUseParentHandlers(true);*/
			//System.setOut(new PrintStream(myLog));
			//System.setErr(new PrintStream(myLog));
			//DedicatedServer.logger.warning("[BukkitAPI] Trying to override loggers... Might fail!");
			//DedicatedServer.logger = new BukkitMCLogger(BukkitContainer.bukkitLogger, "Minecraft");
			//ServerGUI.logger = DedicatedServer.logger;
			/*Logger stdOut = Logger.getLogger("STDOUT");
	        stdOut.setParent(BukkitContainer.bukkitLogger);
	        Logger stdErr = Logger.getLogger("STDERR");
	        stdErr.setParent(BukkitContainer.bukkitLogger);*/
		}
		//FMLCommonHandler.instance().getFMLLogger().addHandler(new BukkitGuiForwarder());
		try {
			BukkitContainer.bServer = new BukkitServer(MinecraftServer.getServer(), MinecraftServer.getServer().getConfigurationManager());
		}
		catch (Exception e) {
			e.printStackTrace();
			//MinecraftException ex = new MinecraftException("Failed to launch the Bukkit API!");
			CrashReport c = new CrashReport("The mod Bukkit4Vanilla failed to launch", e);
			//c.("Bukkit API Details", "Error encountered: " + e.getStackTrace());
			c.saveToFile(c.getFile());
			MinecraftServer.logger.severe(c.getCompleteReport());
			MinecraftServer.logger.severe("You game will now be stopped. Please report this to the mod developer.");
			
//			MinecraftServer.getServer().stopServer();
		}

	}

}
