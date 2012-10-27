package keepcalm.mods.bukkit.forgeHandler.commands;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public abstract class BukkitCommandBase extends CommandBase {
	
	public String joinListOfStrings(String[] str) {
		String ret = "";
		for (String j : str) {
			ret += j + " ";
		}
		return ret.trim();
	}
	
	@Override
	public abstract String getCommandName();

	@Override
	public abstract void processCommand(ICommandSender var1, String[] var2);

}
