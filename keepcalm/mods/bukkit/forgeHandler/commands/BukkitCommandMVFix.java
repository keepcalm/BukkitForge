package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.asm.BukkitContainer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;
/**
 * A command to fix compatibility issues with plugins like MultiVerse - which i __suspect__ use 
 * aliases.
 */
public class BukkitCommandMVFix extends CommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "bexec";
	}
	
	public String getCommandUsage(ICommandSender var1) {
		return "/bexec <bukkit command> <arguments>";
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		BukkitContainer.bServer.getRealCmdMap().dispatch(new BukkitCommandSender(var1), this.joinString(var2, 0));
	}

}
