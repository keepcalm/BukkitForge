package keepcalm.mods.bukkit.asm;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import net.minecraft.src.DedicatedServer;

public class BukkitGuiForwarder extends Handler {

	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord arg0) {
		DedicatedServer.logger.log(arg0.getLevel(), String.format("[%s] %s", new Object[] {arg0.getLoggerName(), arg0.getMessage()}));
	}

}
