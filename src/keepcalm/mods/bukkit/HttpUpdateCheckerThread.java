package keepcalm.mods.bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.ChatColor;

import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;

public class HttpUpdateCheckerThread implements Runnable {

	public static boolean ENABLED = false;
	
	private static URL url;
	
	static {
		try {
			url = (new URL("http://build.technicpack.net/job/BukkitForge/ws/build/dist/LATEST_BUILD"));
			ENABLED = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			ENABLED = false;
		}
	}
		
	private void broadcastUpdateToConsole(String newVer) {
		BukkitContainer.bukkitLogger.info("A new version of BukkitForge is available: " + newVer + ". You may wish to upgrade to this build, since it will probably contain Bugfixes :P");
	}
	
	private void broadcastUpdateToPlayers(String newVer) {
		BukkitContainer.bServer.broadcastMessage(ChatColor.GREEN + "[BukkitForge] A new version is available: " + newVer + ". It will probably contain bugfixes." + ChatColor.RESET);
	}
	
	@Override
	public void run() {
		if (!ENABLED) {
			return;
		}
		while (!ForgeEventHandler.ready) {}
		
		while (ForgeEventHandler.ready) {
			try {
				URLConnection con = url.openConnection();
				BufferedReader buf = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line = buf.readLine();
				if (!line.startsWith(BukkitContainer.BF_FULL_VERSION)) {
					// new version!
					switch(BukkitContainer.UPDATE_ANNOUNCE_METHOD) {
					case -1:
						this.broadcastUpdateToConsole(line);
						this.broadcastUpdateToPlayers(line);
						break;
					case 0:
						this.broadcastUpdateToPlayers(line);
						break;
					case 1:
						this.broadcastUpdateToConsole(line);
						break;
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// seconds
			try {
				Thread.sleep(BukkitContainer.UPDATE_CHECK_INTERVAL * 1000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
