package keepcalm.mods.bukkit.forgeHandler.commands;

import java.util.List;

import keepcalm.mods.bukkit.bukkitAPI.BukkitConsoleCommandSender;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntity;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

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
	public final String name;
	//private Permission requiredPerms;
	//private final CommandExecutor myExec;
	public final Command bukkitCommandInstance;
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
		
		if (who instanceof MinecraftServer) {
			return bukkitCommandInstance.tabComplete(BukkitConsoleCommandSender.getInstance() ,name , args);
		}
		else {
			BukkitPlayer player = (BukkitPlayer) BukkitEntity.getEntity(BukkitServer.instance(), (EntityPlayer) who);
			return bukkitCommandInstance.tabComplete(player, name, args);
		}
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
	
	@SuppressWarnings("all")
	public boolean canCommandSenderUseCommand(ICommandSender who) {
		
		CommandSender sender;
		if (who instanceof EntityPlayerMP) {
			sender = new BukkitPlayer((EntityPlayerMP) who);
		}
		else {
			sender = BukkitConsoleCommandSender.getInstance();
		}
		if ((bukkitCommandInstance.testPermissionSilent(sender)) 
				|| sender.hasPermission(bukkitCommandInstance.getPermission()) || sender.isOp() 
				|| bukkitCommandInstance.getPermission() == null 
				|| bukkitCommandInstance.getPermission().isEmpty() 
				|| (MinecraftServer.getServer().getServerOwner().equalsIgnoreCase(sender.getName())) 
				|| MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(sender.getName().toLowerCase())) {
			return true;
		}
		//System.out.println("NO! For " + name);
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
			if (var1 instanceof EntityPlayerMP) {
				sender = new BukkitPlayer((EntityPlayerMP) var1);
			}
			else {
				sender = BukkitConsoleCommandSender.getInstance();
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
