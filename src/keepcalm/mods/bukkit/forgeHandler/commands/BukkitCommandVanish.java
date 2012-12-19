package keepcalm.mods.bukkit.forgeHandler.commands;

import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.forgeHandler.VanishUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// DEBUG ONLY
public class BukkitCommandVanish extends Command {

	public BukkitCommandVanish() {
		super("vanish");
	}
	


	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		if (sender instanceof Player) {
			Player who = (Player) sender;
			
			for (Player j : who.getWorld().getPlayers()) {
				if (j == who)
					continue;
				if (VanishUtils.isHidden(((BukkitPlayer) who).getHandle(), ((BukkitPlayer) j).getHandle()))
						j.showPlayer(who);
				else
					j.hidePlayer(who);
			}
		}
		return true;
	}



}
