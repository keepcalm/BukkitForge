package keepcalm.mods.bukkit.asm;

import java.util.logging.Level;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CrashReport;
import net.minecraftforge.event.world.ChunkDataEvent.Load;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class BukkitStarter implements Runnable {

	@Override
	public void run() {
		try {
			
			BukkitContainer.bServer = new BukkitServer();
		}
		catch (Exception e) {
			
			FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, "Something real bad happened! The BukkitAPI will not be running for this Minecraft session.", e);
//			MinecraftServer.getServer().stopServer();
		}

	}

}
