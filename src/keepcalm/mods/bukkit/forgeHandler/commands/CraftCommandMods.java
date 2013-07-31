package keepcalm.mods.bukkit.forgeHandler.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

import org.bukkit.ChatColor;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class CraftCommandMods extends CommandBase {

	@Override
	public String getCommandName() {
		return "mods";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		var1.sendChatToPlayer(ChatMessageComponent.func_111066_d(ChatColor.YELLOW + "Installed Mods: "));
		for (ModContainer i : Loader.instance().getIndexedModList().values()) {
			var1.sendChatToPlayer(ChatMessageComponent.func_111066_d(ChatColor.GREEN + i.getName() + ChatColor.RESET + ", version " + ChatColor.YELLOW + i.getDisplayVersion() + ChatColor.RESET ));
			
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return null;
	}

}



