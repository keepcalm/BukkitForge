package keepcalm.mods.bukkit.forgeHandler.commands;

import java.util.Set;

import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

@Deprecated
public class BukkitCommandSender implements CommandSender {
	private BukkitPlayer realPlayer;
	private ConsoleCommandSender cons;
	private boolean isConsole = false;
	public BukkitCommandSender(ICommandSender guy) {
		this.realPlayer = new BukkitPlayer((EntityPlayerMP) guy);
	}
	@Override
	public boolean isPermissionSet(String name) {
		return isConsole ? cons.isPermissionSet(name): realPlayer.isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return isConsole ? cons.isPermissionSet(perm) : realPlayer.isPermissionSet(perm);
	}

	@Override
	public boolean hasPermission(String name) {
		return isConsole ? cons.hasPermission(name) : realPlayer.hasPermission(name);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return isConsole ? cons.hasPermission(perm) : realPlayer.hasPermission(perm);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value) {
		return isConsole ? cons.addAttachment(plugin, name, value) : realPlayer.addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return isConsole ? cons.addAttachment(plugin) : realPlayer.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value, int ticks) {
		return isConsole ? cons.addAttachment(plugin, name, value, ticks) : realPlayer.addAttachment(plugin, name, value, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return isConsole ? cons.addAttachment(plugin, ticks) : realPlayer.addAttachment(plugin, ticks);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		if (isConsole) cons.removeAttachment(attachment);
		else realPlayer.removeAttachment(attachment);

	}

	@Override
	public void recalculatePermissions() {
		if (isConsole) cons.recalculatePermissions();
		else realPlayer.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		if (isConsole) return cons.getEffectivePermissions();
		else return realPlayer.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		if (isConsole) return true;
		return realPlayer.isOp();
	}

	@Override
	public void setOp(boolean value) {
		if (isConsole) throw new IllegalArgumentException("Can't change op status of console!");
		realPlayer.setOp(value);
	}

	@Override
	public void sendMessage(String message) {
		if (isConsole) cons.sendMessage(message);
		else realPlayer.sendMessage(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		if (isConsole) cons.sendMessage(messages); else
		realPlayer.sendMessage(messages);

	}

	@Override
	public Server getServer() {
		if (isConsole) return cons.getServer();
		return realPlayer.getServer();
	}

	@Override
	public String getName() {
		if (isConsole) return cons.getName();
		return realPlayer.getName();
	}

}
