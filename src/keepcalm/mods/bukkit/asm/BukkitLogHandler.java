package keepcalm.mods.bukkit.asm;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import net.minecraft.server.MinecraftServer;

public class BukkitLogHandler extends Handler {

	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord arg0) {
		if (arg0.getLoggerName().equalsIgnoreCase("STDOUT") || arg0.getLoggerName().equalsIgnoreCase("STDERR")) {
			MinecraftServer.logger.log(arg0.getLevel(), "[" + arg0.getLoggerName() + "] " + arg0.getMessage());
		}
	}

}
