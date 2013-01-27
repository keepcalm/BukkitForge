package org.bukkit.craftbukkit;

import java.util.logging.Logger;

import keepcalm.mods.bukkit.BukkitContainer;
import net.minecraft.server.dedicated.DedicatedServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class BukkitLogHandler extends Logger {
	private static final Logger theRealLogger = BukkitContainer.bukkitLogger;
	private static final Logger minecraftLogger = DedicatedServer.logger;
	private boolean isGuiOn = DedicatedServer.getServer().getGuiEnabled();
	protected BukkitLogHandler(String name, String resourceBundleName) {
		super(name, resourceBundleName);
		
		
	}
	public BukkitLogHandler(String name) {
		super(name, FMLCommonHandler.instance().getFMLLogger().getResourceBundleName());
	}
	public BukkitLogHandler() {
		super("__DUMMY__", FMLCommonHandler.instance().getFMLLogger().getResourceBundleName());
	}
	@Override
	public void fine(String message) {
		theRealLogger.fine(message);
		if (isGuiOn) minecraftLogger.fine("[BukkitAPI] " + message);
	}
	@Override
	public void finer(String message) {
		theRealLogger.finer(message);
		if (isGuiOn) minecraftLogger.finer("[BukkitAPI] " +message);
	}
	@Override
	public void finest(String message) {
		theRealLogger.finest(message);
		if (isGuiOn) minecraftLogger.finest("[BukkitAPI] " + message);
	}
	@Override
	public void warning(String message) {
		theRealLogger.warning(message);
		if (isGuiOn) minecraftLogger.warning("[BukkitAPI] " + message);
	}
	@Override
	public void severe(String message) {
		theRealLogger.severe(message);
		if (isGuiOn) minecraftLogger.severe("[BukkitAPI] " + message);
	}
	@Override
	public void info(String message) {
		theRealLogger.info(message);
		if (isGuiOn) minecraftLogger.info("[BukkitAPI] " + message);
	}

}
