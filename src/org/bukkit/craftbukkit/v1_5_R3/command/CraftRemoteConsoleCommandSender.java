package org.bukkit.craftbukkit.v1_5_R3.command;

import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.ChatMessageComponent;

import org.bukkit.command.RemoteConsoleCommandSender;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
    public CraftRemoteConsoleCommandSender() {
        super();
    }

    public void sendMessage(String message) {
        RConConsoleSource.consoleBuffer.sendChatToPlayer(ChatMessageComponent.func_111066_d(message + "\n")); // Send a newline after each message, to preserve formatting.
    }
    
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public String getName() {
        return "Rcon";
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
    }
}
