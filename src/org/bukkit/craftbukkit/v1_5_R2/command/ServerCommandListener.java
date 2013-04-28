package org.bukkit.craftbukkit.v1_5_R2.command;

import java.lang.reflect.Method;


import org.bukkit.command.CommandSender;

public class ServerCommandListener implements net.minecraft.command.ICommandSender/*was:ICommandListener*/ {
    private final CommandSender commandSender;
    private final String prefix;

    public ServerCommandListener(CommandSender commandSender) {
        this.commandSender = commandSender;
        String[] parts = commandSender.getClass().getName().split("\\.");
        this.prefix = parts[parts.length - 1];
    }

    public void sendChatToPlayer/*was:sendMessage*/(String msg) {
        this.commandSender.sendMessage(msg);
    }

    public CommandSender getSender() {
        return commandSender;
    }

    public String getCommandSenderName/*was:getName*/() {
        try {
            Method getName = commandSender.getClass().getMethod("getName");

            return (String) getName.invoke(commandSender);
        } catch (Exception e) {}

        return this.prefix;
    }

    public String translateString/*was:a*/(String s, Object... aobject) {
        return net.minecraft.util.StringTranslate/*was:LocaleLanguage*/.getInstance/*was:a*/().translateKeyFormat/*was:a*/(s, aobject);
    }

    public boolean canCommandSenderUseCommand/*was:a*/(int i, String s) {
        return true;
    }

    public net.minecraft.util.ChunkCoordinates/*was:ChunkCoordinates*/ getPlayerCoordinates/*was:b*/() {
        return new net.minecraft.util.ChunkCoordinates/*was:ChunkCoordinates*/(0, 0, 0);
    }
}
