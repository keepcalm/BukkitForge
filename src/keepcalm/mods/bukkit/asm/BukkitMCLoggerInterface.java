package keepcalm.mods.bukkit.asm;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BukkitMCLoggerInterface extends Handler {
	private Logger theGuiLog;
	private String prefix;
	public BukkitMCLoggerInterface(String prefix, Logger myLog) {
		this.theGuiLog = myLog;
		this.prefix = prefix;
	}
	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord arg0) {
		theGuiLog.log(arg0.getLevel(), "[" + prefix + "] " + arg0.getMessage());
	}
	

}
