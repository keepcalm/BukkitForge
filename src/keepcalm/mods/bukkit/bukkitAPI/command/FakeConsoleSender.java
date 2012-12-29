package keepcalm.mods.bukkit.bukkitAPI.command;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.conversations.ConversationTracker;
import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;

public class FakeConsoleSender extends ServerCommandSender implements ConsoleCommandSender {

	private EntityPlayerMP thePlayer;
	private BukkitServer theServer;
	
	protected final ConversationTracker convo = new ConversationTracker();
	
	public FakeConsoleSender(Server server, EntityPlayerMP player) {
		theServer = (BukkitServer) server;
		thePlayer = player;
	}
	
	@Override
	public void sendMessage(String message) {
		sendRawMessage(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		for (String i : messages) {
			sendMessage(i);
		}
	}

	@Override
	public Server getServer() {
		return theServer;
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean value) {
		thePlayer.sendChatToPlayer("Console got deopped (not really :P)");
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
		thePlayer.sendChatToPlayer(message);
	}



}
