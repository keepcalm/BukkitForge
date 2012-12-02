package keepcalm.mods.bukkit.forgeHandler.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.google.common.base.Joiner;
/**
 * 
 * A basic Bukkit2ICommand command handler
 * This won't work with Multiverse until I work out the aliases.
 * So I'll probably add a command that does this properly, until I work out how
 * @author keepcalm
 *
 */
public class CommandExecutor2CommandBase extends CommandBase {
	private String name;
	//private Permission requiredPerms;
	//private final CommandExecutor myExec;
	private final Command bukkitCommandInstance;
	/**
	 * Initialises a new instance of a forge-friendly bukkit command handler.
	 * 
	 * 
	 * @param theBukkitExecer - the command that this instance will handle
	 * @param name - the name of the command
	 */
	public CommandExecutor2CommandBase(Command cmd, String name) {
		/*this.name = name;
		this.requiredPerm = permissionWanted;*/
		this.bukkitCommandInstance = cmd;
		//this.myExec = cmd.
		this.name =name;
		//this.requiredPerms = wantedPerms;
		//this.bukkitCommandInstance = Bukkit.getServer().getPluginCommand(name);
		
	}
	
	public List<String> addTabCompletionOptions(ICommandSender who, String[] args) {
		
		return bukkitCommandInstance.tabComplete(new BukkitCommandSender(who),name , args);
	}
	
	public List<String> getCommandAliases() {
		//bukkitCommandInstance.
		return bukkitCommandInstance.getAliases();
	}
	
	public String getCommandUsage(ICommandSender who) {
		String usage = bukkitCommandInstance.getUsage();
		usage = usage.replace("<command>", name);
		//System.out.println(usage);
		return usage;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender who) {
		BukkitCommandSender j = new BukkitCommandSender(who);
		if ((bukkitCommandInstance.testPermissionSilent(j))) {
			return true;
		}
		return false;
		
	}
	
	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try {
			//System.out.println("Begin execution: " + name + " " + Joiner.on(' ').join(var2));
			bukkitCommandInstance.execute(new BukkitCommandSender(var1), this.name, var2);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

	}
	public void execute(CommandSender g, String usedName, String[] args) {
		//System.out.println("Begin execution: " + usedName + " " + Joiner.on(' ').join(args));
		bukkitCommandInstance.execute(g, usedName, args);
	}

}
