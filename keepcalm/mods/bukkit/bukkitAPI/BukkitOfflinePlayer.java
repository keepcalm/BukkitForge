package keepcalm.mods.bukkit.bukkitAPI;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.AnvilSaveHandler;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IPlayerFileData;
import net.minecraft.src.NBTTagCompound;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BukkitOfflinePlayer implements OfflinePlayer {
	private BukkitServer theBServer;
	private String name;
	private boolean isOp = false;
	private boolean isOnline = false;
	private boolean banned = false;
	private boolean whitelisted = true;
	private boolean isFirstTime = false;
	private long firstLoginTime;
	private long lastLoginTime;
	private static AnvilSaveHandler playerNBTManager;
	public static HashMap<String,BukkitOfflinePlayer> instances = new HashMap<String,BukkitOfflinePlayer>();
	
	
	public BukkitOfflinePlayer(BukkitServer server, String name) {
		this.theBServer = server;
		if (getPlayerDats().containsKey(name)) {
			// player dat exists...
			if (theBServer.getHandle().getDedicatedPlayerList().getPlayerForUsername(name) != null) {
				this.isOnline = true;
				this.name = name;
				this.isOp = theBServer.getHandle().getConfigurationManager().getOps().contains(name.toLowerCase());
				this.whitelisted = theBServer.getHandle().getConfigurationManager().getWhiteListedPlayers().contains(name.toLowerCase());
			}
			else {
				NBTTagCompound playerNBT;
				try
		        {
		            File var2 = new File(theBServer.getWorldContainer(), "players/" + name.toLowerCase() + ".dat");

		            if (var2.exists())
		            {
		                 playerNBT = CompressedStreamTools.readCompressed(new FileInputStream(var2));
		            }
		            else {
		            	emergencyRegisterUnknownPlayer(name);
		            	return;
		            }
		        }
		        catch (Exception var3)
		        {
		            theBServer.getLogger().warning("Failed to load player data for " + name);
		        }
				
			}
		}
	}

	private void emergencyRegisterUnknownPlayer(String name2) {
		// TODO Auto-generated method stub
		
	}

	protected HashMap<String,File> getPlayerDats() {
		String[] files = new File(theBServer.getWorldContainer(), "/players").list(new keepcalm.mods.bukkit.utils.DatFileFilter());
		HashMap<String,File> dats = new HashMap<String,File>();
		String prepend = theBServer.getWorldContainer().getAbsolutePath() + "/players";
		for (String fileName : files) {
			dats.put(fileName.substring(0, (fileName.length() - 4)), new File(prepend, fileName));
		}
		return dats;
	}
	@Override
	public boolean isOp() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void setOp(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	@Override
	public boolean isOnline() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public boolean isBanned() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBanned(boolean banned) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isWhitelisted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWhitelisted(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Player getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getFirstPlayed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLastPlayed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasPlayedBefore() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Location getBedSpawnLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
