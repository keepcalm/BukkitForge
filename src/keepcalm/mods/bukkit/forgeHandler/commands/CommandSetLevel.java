package keepcalm.mods.bukkit.forgeHandler.commands;

import org.bukkit.craftbukkit.BukkitServer;

import keepcalm.mods.bukkit.forgeHandler.commands.CommandRequirementRegistry.Level;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandSetLevel extends BukkitCommandBase {

	public CommandSetLevel() {
		CommandRequirementRegistry.setDefaultRequirement(getClass().getName(), Level.OP);
	}

	@Override
	public String getCommandName() {
		return "level";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender who) {
		return CommandRequirementRegistry.doesCommandSenderHaveLevel(who, getClass().getName());
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 2) {
			throw new WrongUsageException("/level [set|get] COMMAND <OP|ALL|NOBODY>");
		}

		String action = var2[0];
		String targ = var2[1];
		String level = "";
		if (action.toLowerCase().equals("set")) {
			level = var2[2];
		}
		
		String targClazz;
		CommandExecutor2CommandBase cmd;
		try {
			cmd = BukkitServer.instance().getRealCmdMap().getNMSCommand(targ);
			targClazz = cmd.bukkitCommandInstance.getClass().getName();
		}
		catch (Exception e) {
			throw new WrongUsageException("No such command: " + targ);
		}
		if (action.toLowerCase().equals("set")) {
			Level lvl = Level.OP;
			try {
				lvl = Level.valueOf(level);
			}
			catch (Exception e) {
				throw new WrongUsageException("Level must be one of OP, ALL, or NOBODY");
			}
			
			CommandRequirementRegistry.setRequirement(targClazz, lvl);
			var1.sendChatToPlayer("Done!");
		}
		else if (action.equalsIgnoreCase("get")) {
			var1.sendChatToPlayer("To use " + targ + " you need the level " + Level.getStringRepresentation(CommandRequirementRegistry.getRequirement(targClazz)));
		}
	}

}
