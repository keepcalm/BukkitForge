package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.help.SimpleHelpMap;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public class BukkitCommandHelp extends CommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "bhelp";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		SimpleHelpMap hm = (SimpleHelpMap) BukkitServer.instance().getHelpMap();
		CommandSender pcs = new BukkitCommandSender(var1);
		if (var2.length == 0) {

			for (HelpTopic h : hm.getHelpTopics()) {
				if (h.canSee(pcs)) {
					var1.sendChatToPlayer(h.getShortText());
				}
			}
		}
		else {
			String ht = var2[0];
			hm.g
			
		}
	}

}
