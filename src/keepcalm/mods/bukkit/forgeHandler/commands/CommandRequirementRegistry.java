package keepcalm.mods.bukkit.forgeHandler.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import keepcalm.mods.bukkit.asm.BukkitContainer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandRequirementRegistry {
	public static enum Level {
		OP,
		ALL,
		OWNER,
		NOBODY;

		public static String getStringRepresentation(Level lvl) {
			switch (lvl) {
			case OP:
				return "OP";
			case ALL:
				return "EVERYBODY";
			case NOBODY:
				return "NOBODY";
			case OWNER:
				return "OWNER";
			}
			return "UNKNOWN";
		}
	}
	
	private static final HashMap<String,Level> perms = new HashMap<String, CommandRequirementRegistry.Level>();
	private static boolean loaded = false;
	private static final Properties thePermissionsFile = new Properties();
	
	private static File targFile ;
	public static void load() {
		
		targFile = MinecraftServer.getServer().getFile("permissions.props");
		
		try {
			targFile.createNewFile();
			thePermissionsFile.load(new FileInputStream(targFile));
		} catch (IOException e) {
			BukkitContainer.bukkitLogger.log(java.util.logging.Level.WARNING, "Failed to read permissions, using defaults...", e);
		}
		
		for (Object i : thePermissionsFile.keySet()) {
			String j = (String) i;
			try {
				perms.put(j, Level.valueOf(thePermissionsFile.getProperty(j)));
			}
			catch (Exception e) {
				BukkitContainer.bukkitLogger.warning("Invalid level: " + thePermissionsFile.getProperty(j));
			}
		}
		loaded = true;
	}
	
	public static void save() {
		if (!loaded) {
			targFile = MinecraftServer.getServer().getFile("permissions.props");
			BukkitContainer.bukkitLogger.warning("Have not loaded permissions, overwriting...");
		}
		
			try {
				targFile.createNewFile();
				thePermissionsFile.store(new FileOutputStream(targFile), "Command permissions for Bukkit4Vanilla.\nFormat is class name -> OP, ALL, NOBODY or OWNER");
			} catch (IOException e) {
				BukkitContainer.bukkitLogger.log(java.util.logging.Level.WARNING, "FAILED to save command permissions!", e);
			}
		
	}
	
	public static void setRequirement(String commandClassName, Level neededLevel) {
		if (!loaded)
			load();
		perms.put(commandClassName, neededLevel);
	}
	
	public static void setDefaultRequirement(String commandClassName, Level neededLevel) {
		if (!loaded)
			load();
		if (!perms.containsKey(commandClassName))
			setRequirement(commandClassName, neededLevel);
		
	}
	
	public static Level getRequirement(String commandClassName) {
		if (!loaded)
			load();
		if (!perms.containsKey(commandClassName)) {
			return Level.OP;
		}
		else {
			return perms.get(commandClassName);
		}
	}
	
	public static boolean doesCommandSenderHaveLevel(ICommandSender who, String commandClassName) {
		if (!loaded)
			load();
		Level needed = getRequirement(commandClassName);
		
		switch (needed) {
		case ALL:
			return true;
		case NOBODY:
			return false;
		case OP:
			return who instanceof EntityPlayer ? MinecraftServer.getServer().getConfigurationManager().getOps().contains(who.getCommandSenderName()) : true;
		case OWNER:
			return MinecraftServer.getServer().getServerOwner() == who.getCommandSenderName() || !(who instanceof EntityPlayer);
		}
		return false;
	}
}
