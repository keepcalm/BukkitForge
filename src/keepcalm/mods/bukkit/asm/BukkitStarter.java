package keepcalm.mods.bukkit.asm;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.RelaunchClassLoader;

public class BukkitStarter implements Runnable {

	@Override
	public void run() {
		try {
			URL[] urls = ((URLClassLoader)getClass().getClassLoader()).getURLs();
			BukkitClassLoader newCL = new BukkitClassLoader(urls, getClass().getClassLoader());
			newCL.loadClass("keepcalm.mods.bukkit.bukkitAPI.BukkitServer").getMethod("bukkitReEntry").invoke(null);
			// hopefully this works...
		}
		catch (Exception e) {
			
			FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, "Something real bad happened! The BukkitAPI will not be running for this Minecraft session.", e);
//			MinecraftServer.getServer().stopServer();
		}

	}

}
