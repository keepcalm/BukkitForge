package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitConsoleCommandSender;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.command.CommandSender;
/**
 * A command to fix compatibility issues with plugins like MultiVerse - which i __suspect__ use 
 * aliases.
 */
public class BukkitCommandMVFix extends BukkitCommandBase {
	
	private boolean hasBeenAdded = false;
	
	public BukkitCommandMVFix() {
		
		
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
			sender = BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) var1);
		else
			sender = BukkitConsoleCommandSender.getInstance();
			BukkitContainer.bServer.getRealCmdMap().dispatch(sender, this.joinListOfStrings(var2));
		
	}

}
