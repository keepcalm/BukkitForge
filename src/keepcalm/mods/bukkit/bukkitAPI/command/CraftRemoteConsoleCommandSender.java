package keepcalm.mods.bukkit.bukkitAPI.command;

import net.minecraft.network.rcon.RConConsoleSource;

import org.bukkit.command.RemoteConsoleCommandSender;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
    public CraftRemoteConsoleCommandSender() {
        super();
    }

    public void sendMessage(String message) {
        RConConsoleSource.consoleBuffer.sendChatToPlayer(message + "\n"); // Send a newline after each message, to preserve formatting.
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
