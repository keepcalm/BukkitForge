package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.command.FakeConsoleSender;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;

/**
 * Only registered on IntegratedServers
 * @author keepcalm
 *
 */
public class BukkitCommandConsole extends BukkitCommandBase {

	@Override
	public String getCommandName() {
		return "console";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		// only server owner
		return BukkitServer.instance().getHandle().getServerOwner().equalsIgnoreCase(var1.getCommandSenderName());
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length == 0) {
			throw new WrongUsageException("/console <bukkit command to run as console>");
		}
		if (BukkitServer.instance().getRealCmdMap().getNMSCommand(var2[0]) != null) {
			BukkitServer.instance().getRealCmdMap().dispatch(new FakeConsoleSender(Bukkit.getServer(), (EntityPlayerMP) var1), this.joinListOfStrings(var2));
		}
		else {
			
			MinecraftServer.getServer().getConfigurationManager().addOp(var1.getCommandSenderName().toLowerCase());
			MinecraftServer.getServer().getCommandManager().executeCommand(var1, this.joinListOfStrings(var2));
			
		}
	}
	
	
	
}
