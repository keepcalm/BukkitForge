package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.CraftConsoleCommandSender;
/**
 * A command to fix compatibility issues with plugins like MultiVerse - which i __suspect__ use 
 * aliases.
 */
public class CraftCommandMVFix extends CraftCommandBase {
	
	//private boolean hasBeenAdded = false;
	
	public CraftCommandMVFix() {
		
		
	}
	
	@Override
	public String getCommandName() {
		
		return "bexec";
	}
	
	public String getCommandUsage(ICommandSender var1) {
		return "/bexec <bukkit command> <arguments>";
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		CommandSender sender;
		if (var1 instanceof EntityPlayerMP) 
			sender = BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) var1);
		else
			sender = CraftConsoleCommandSender.instance();
			BukkitContainer.bServer.getRealCmdMap().dispatch(sender, this.joinListOfStrings(var2));
		
	}

}
