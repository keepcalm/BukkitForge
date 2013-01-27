package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.BukkitContainer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandServerStop;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.StopCommand;
/**
 * Shuts down the server in a bukkit-friendly way.
 * @author keepcalm
 *
 */
public class BukkitCommandStop extends CommandBase {

	@Override
	public String getCommandName() {
		return "stop";
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		CommandSender s;
		if (sender instanceof EntityPlayerMP) {
			s = BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) sender);
		}
		else s = BukkitConsoleCommandSender.getInstance();
		if ((new StopCommand()).testPermissionSilent(s)) {
			return true;
		}
		return false;
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		BukkitContainer.bServer.shutdown();
		(new CommandServerStop()).processCommand(var1, var2);
	}

}
