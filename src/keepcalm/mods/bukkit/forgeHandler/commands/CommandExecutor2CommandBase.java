package keepcalm.mods.bukkit.forgeHandler.commands;

import java.util.List;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.DedicatedServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.IntegratedServer;
import net.minecraft.src.RConConsoleSource;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
		CommandSender sender;
		try {
			sender = new BukkitCommandSender(who);
		}
		catch (ClassCastException e) {
			sender = BukkitServer.instance().getConsoleSender();
		}
		if ((bukkitCommandInstance.testPermissionSilent(sender))) {
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
			CommandSender sender;
			try {
				sender = new BukkitCommandSender(var1);
				System.out.println("NOT a console!");
			}
			catch (ClassCastException e) {
				sender = BukkitServer.instance().getConsoleSender();
			}
			bukkitCommandInstance.execute(sender, this.name, var2);
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
