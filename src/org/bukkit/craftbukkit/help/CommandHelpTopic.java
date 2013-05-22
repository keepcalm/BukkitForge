package org.bukkit.craftbukkit.help;

import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public class CommandHelpTopic extends HelpTopic {
	protected String perm;
	public CommandHelpTopic(String name, String descr, String perm, String longDescr)  {
		super.name = name;
		super.shortText = descr;
		super.fullText = longDescr;
		this.perm = perm;
	}
	
	
	@Override
	public boolean canSee(CommandSender player) {
		return player.hasPermission(perm);
	}

}
