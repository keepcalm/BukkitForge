package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.bukkitAPI.BukkitConsoleCommandSender;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.bukkitAPI.help.SimpleHelpMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public class BukkitCommandHelp extends CommandBase {
	public static int entries_per_page = 5;
	
	
	@Override
	public String getCommandName() {
		return "bhelp";
	}
	
	@Override
	public String getCommandUsage(ICommandSender whom) {
		return "/bhelp [command]";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		SimpleHelpMap hm = (SimpleHelpMap) BukkitServer.instance().getHelpMap();
		CommandSender pcs = var1 instanceof EntityPlayer ? new BukkitPlayer(BukkitServer.instance(), (EntityPlayerMP) var1) : BukkitConsoleCommandSender.getInstance();
		int total = hm.getHelpTopics().size();
		// round up
		int totalPages = MathHelper.ceiling_float_int(total / entries_per_page);
		if (var2.length == 0) {
			int i = 0;
			var1.sendChatToPlayer("Page " + ChatColor.YELLOW + "1" + ChatColor.RESET + " of " + ChatColor.YELLOW + totalPages);
			for (HelpTopic h : hm.getHelpTopics()) {
				if (i == entries_per_page) {
					var1.sendChatToPlayer("Type " + ChatColor.GREEN + "/bhelp 2" + ChatColor.RESET + " to see the next page!");
				}
				if (h.canSee(pcs)) {
					var1.sendChatToPlayer(h.getShortText());
					i++;
				}
			}
		}
		else {
			String ht = var2[0];
			ht = ht.trim();
			try {
				int page = Integer.parseInt(ht);
				int helpTopicStart = entries_per_page * page;
				int helpTopicEnd = entries_per_page + helpTopicStart;
				int i = 0;
				for (HelpTopic x : hm.getHelpTopics()) {
					if (i < helpTopicStart) {
						i++;
						continue;
					}
					if (i > helpTopicEnd)
						break;
					if (x.canSee(pcs)) {
						var1.sendChatToPlayer(x.getShortText());
						i++;
					}
					
				}
			}
			catch (NumberFormatException e) {
				if (hm.getHelpTopic(ht) == null) {
					// fallback
					var1.sendChatToPlayer(ChatColor.RED + "No such bukkit command registered, trying vanilla /help command!");
					MinecraftServer.getServer().getCommandManager().executeCommand(var1, "/help " + ht);
				}
				else {
					var1.sendChatToPlayer(hm.getHelpTopic(ht).getFullText(pcs));
				}
			}
			
		}
	}

}
