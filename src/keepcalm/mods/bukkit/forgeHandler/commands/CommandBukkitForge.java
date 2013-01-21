package keepcalm.mods.bukkit.forgeHandler.commands;

import java.util.Arrays;
import java.util.List;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitConsoleCommandSender;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.command.CommandSender;


/**
 * control for BukkitForge from console/in-game
 * 
 * @author keepcalm
 *
 */
public class CommandBukkitForge extends BukkitCommandBase {

	public static enum EnumState {
		// hehe
		ON(new String[] {"on", "enable", "yes", "ja", "true"}, true),
		UNKNOWN(new String[] {}, false),
		OFF(new String[] {"off", "disable", "no", "nein", "false"}, false);
		
		private List<String> objs;
		private boolean state;
		private EnumState(String[] aliases, boolean val) {
			objs = Arrays.asList(aliases);
			state = val;
		}
		
		public boolean doesThisTextMatch(String par1) {
			return objs.contains(par1.toLowerCase());
		}
		
		public boolean getState() {
			return state;
		}
		
		public boolean isUnknown() {
			return this == UNKNOWN;
		}
		
		public boolean isOn() {
			return this == ON;
		}
		
		public boolean isOff() {
			return this == OFF;
		}
		
		public static EnumState getState(String par1) {
			if (ON.doesThisTextMatch(par1)) {
				return ON;
			}
			else if (OFF.doesThisTextMatch(par1)) {
				return OFF;
			}
			return UNKNOWN;
		}
	}
	
	@Override
	public String getCommandName() {
		return "/bukkitforge";
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1) {
		return "/bukkitforge help";
	}
	
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1) {
		CommandSender x;
		if (par1 instanceof EntityPlayerMP) {
			x = BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) par1);
		}
		else {
			x = BukkitConsoleCommandSender.getInstance();
		}
		if (x.hasPermission("bukkitforge.admin")) return true;
		return false;
		
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length == 0) {
			throw new WrongUsageException("/bukkitforge help");
		}
		
		if (var2[0].equalsIgnoreCase("help")) {
			var1.sendChatToPlayer("Commands:");
			var1.sendChatToPlayer("/bukkitforge debug <on|off>");
		}
		else if (var2[0].equalsIgnoreCase("debug")) {
			if (var2.length < 2) {
				throw new WrongUsageException("/bukkitforge debug <on|off>");
			}
			EnumState onOrOff = EnumState.getState(var2[1]);
			if (onOrOff == EnumState.UNKNOWN) {
				throw new WrongUsageException(var2[1] + " is not a valid state!");
			}
			BukkitContainer.DEBUG = onOrOff.getState();
			var1.sendChatToPlayer("Debug turned " + var2[1]);
		}
		
	}

}
