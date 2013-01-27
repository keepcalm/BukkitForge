package org.bukkit.craftbukkit.command;

import keepcalm.mods.bukkit.BukkitContainer;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.conversations.ConversationTracker;
//import org.bukkit.craftbukkit.conversations.ConversationTracker;

/**
 * Represents CLI input from a console
 */
public class BukkitConsoleCommandSender extends ServerCommandSender implements ConsoleCommandSender {

    protected final ConversationTracker conversationTracker = new ConversationTracker();

    private static final BukkitConsoleCommandSender instance = (BukkitConsoleCommandSender) (BukkitContainer.allowAnsi ? new ColouredConsoleSender() : new BukkitConsoleCommandSender());
    
    protected BukkitConsoleCommandSender() {
        super();
        
    }

    public void sendMessage(String message) {
        sendRawMessage(message);
    }
    
    public void sendRawMessage(String message) {
        System.out.println(ChatColor.stripColor(message));
    }

    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public String getName() {
        return "CONSOLE";
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of server console");
    }

    public boolean beginConversation(Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    public void abandonConversation(Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    public void acceptConversationInput(String input) {
        conversationTracker.acceptConversationInput(input);
    }

    public boolean isConversing() {
        return conversationTracker.isConversing();
    }
    
    public static BukkitConsoleCommandSender instance() {
    	return instance;
    }
    
}
