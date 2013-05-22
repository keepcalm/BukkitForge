package keepcalm.mods.bukkit.forgeHandler.commands;

import java.util.List;

import keepcalm.mods.bukkit.BukkitContainer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftConsoleCommandSender;
import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.google.common.base.Joiner;
/**
 * 
 * A basic Craft2ICommand command handler
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
	 * @param cmd - the command that this instance will handle
	 * @param name - the name of the command
	 */
	public CommandExecutor2CommandBase(Command cmd, String name) {
		this.bukkitCommandInstance = cmd;
		this.name =name;
	}
	
	public List<String> addTabCompletionOptions(ICommandSender who, String[] args) {
		
		if (who instanceof MinecraftServer || who instanceof RConConsoleSource) {
			return bukkitCommandInstance.tabComplete(CraftConsoleCommandSender.getInstance() ,name , args);
		}
		else {
			CraftPlayer player = (CraftPlayer) CraftEntity.getEntity(CraftServer.instance(), (EntityPlayer) who);
			return bukkitCommandInstance.tabComplete(player, name, args);
		}
	}
	
	public List<String> getCommandAliases() {
		return bukkitCommandInstance.getAliases();
	}
	
	public String getCommandUsage(ICommandSender who) {
		String usage = bukkitCommandInstance.getUsage();
		usage = usage.replace("<command>", name);
		return usage;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender who) {
		
		CommandSender sender;
		if (who instanceof EntityPlayerMP) {
			sender = BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) who);
		}
		else {
			sender = CraftConsoleCommandSender.getInstance();
		}
		boolean allowed = bukkitCommandInstance.testPermissionSilent(sender) 
				|| sender.hasPermission(bukkitCommandInstance.getPermission())
				|| bukkitCommandInstance.getPermission().isEmpty();
		if (!(MinecraftServer.getServer() instanceof DedicatedServer)) {
			allowed = allowed || MinecraftServer.getServer().getServerOwner().equalsIgnoreCase(who.getCommandSenderName()) 
					|| MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(who.getCommandSenderName().toLowerCase());
		}
		
		return allowed;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		BukkitContainer.bukkitLogger.info(var1.getCommandSenderName() + " issued server command: " + name + Joiner.on(' ').join(var2));
		try {
			CommandSender sender;
			if (var1 instanceof EntityPlayerMP) {
				sender = BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) var1);
			}
			else {
				sender = CraftConsoleCommandSender.getInstance();
			}
			bukkitCommandInstance.execute(sender, this.name, var2);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

	}
	public void execute(CommandSender g, String usedName, String[] args) {
		bukkitCommandInstance.execute(g, usedName, args);
	}
	
	public List<String> tabComplete(CommandSender who, String name, String[] args) {
		return bukkitCommandInstance.tabComplete(who, name, args);
	}

}
