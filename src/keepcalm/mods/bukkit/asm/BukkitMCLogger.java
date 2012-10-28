package keepcalm.mods.bukkit.asm;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLCommonHandler;

public class BukkitMCLogger extends Logger {
	private Logger log;
	private String prefix;

	public BukkitMCLogger(Logger log, String prefix) {
		super(prefix, FMLCommonHandler.instance().getFMLLogger().getResourceBundleName());
		this.prefix = prefix;
		this.log = log;
		
	}
	
	@Override
	public void log(Level level, String msg) {
		log.log(level, "[" + prefix + "] " + msg);
	}
	@Override
	public void log(Level level, String msg, Throwable t) {
		log.log(level, "[" + prefix + "] " + msg, t);
	}
	@Override
	public void log(Level level, String msg, Object obj) {
		log.log(level, "[" + prefix + "] " + msg, obj);
	}
	
	@Override
	public void finest(String msg) {
		log(Level.FINEST, msg);
	}
	@Override
	public void finer(String msg) {
		this.log(Level.FINER, msg);
	}
	@Override
	public void fine(String msg) {
		this.log(Level.FINE, msg);
	}
	@Override
	public void info(String msg) {
		this.log(Level.INFO, (msg));
	}
	@Override
	public void warning(String msg) {
		log(Level.WARNING, msg);
	}
	@Override
	public void severe(String msg) {
		log(Level.SEVERE, msg);
	}
	@Override
	public void config(String msg) {
		log(Level.CONFIG, msg);
	}
}
