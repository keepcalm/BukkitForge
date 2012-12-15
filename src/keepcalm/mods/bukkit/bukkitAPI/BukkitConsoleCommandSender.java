package keepcalm.mods.bukkit.bukkitAPI;

import java.util.Set;

import net.minecraft.server.dedicated.DedicatedServer;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class BukkitConsoleCommandSender implements ConsoleCommandSender {
	private static BukkitConsoleCommandSender instance = null;
	private DedicatedServer theServer;
	private BukkitServer bServer;
	private final PermissibleBase perms = new PermissibleBase(this);
	protected final BukkitConversationTracker convo = new BukkitConversationTracker();
	
	public static ConsoleCommandSender getInstance() {
		if (instance == null)
			instance = new BukkitConsoleCommandSender(BukkitServer.instance());
		return instance;
	}
	
	public BukkitConsoleCommandSender(BukkitServer server) {
		if (this.instance == null) {
			this.bServer = server;
			this.theServer = server.getHandle();
			this.instance = this;
		}
		else {
			// ignore
			return;
		}
		
	}
	
	@Override
	public void sendMessage(String message) {
	//	C//onsoleLogManager.loggerLogManager.info("[Bukkit API]: " + message);
		sendRawMessage(message);

	}

	@Override
	public void sendMessage(String[] messages) {
		for (String j : messages) {
			//ConsoleLogManager.loggerLogManager.info("[Bukkit API]: " + j );
			sendMessage(j);
		}
	}

	@Override
	public Server getServer() {
		return bServer;
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public boolean isPermissionSet(String name) {
		return perms.isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return perms.isPermissionSet(perm);
	}

	@Override
	public boolean hasPermission(String name) {
		return perms.hasPermission(name);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return perms.hasPermission(perm);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value) {
		return perms.addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return perms.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value, int ticks) {
		return perms.addAttachment(plugin, name, value, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return perms.addAttachment(plugin, ticks);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		perms.removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
			perms.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return perms.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean value) {
			throw new UnsupportedOperationException("Can't change operator status of console!");
	}

	@Override
	public boolean isConversing() {
		return convo.isConversing();
	}

	@Override
	public void acceptConversationInput(String input) {
		convo.acceptConversationInput(input);

	}

	@Override
	public boolean beginConversation(Conversation conversation) {
		return convo.beginConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation) {
		 convo.abandonConversation(conversation, new ConversationAbandonedEvent(conversation));
	}

	@Override
	public void abandonConversation(Conversation conversation,
			ConversationAbandonedEvent details) {
		convo.abandonConversation(conversation, details);
	}

	@Override
	public void sendRawMessage(String message) {
		System.out.println(ChatColor.stripColor(message));
	}

}
