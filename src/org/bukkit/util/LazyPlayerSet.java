package org.bukkit.util;

import java.util.HashSet;
import java.util.List;

import org.bukkit.craftbukkit.CraftPlayerCache;
import org.bukkit.entity.Player;

public class LazyPlayerSet extends LazyHashSet<Player> {

    @Override
    HashSet<Player> makeReference() {
        if (reference != null) {
            throw new IllegalStateException("Reference already created!");
        }
        List<net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/> players = net.minecraft.server.MinecraftServer/*was:MinecraftServer*/.getServer/*was:getServer*/().getConfigurationManager/*was:getPlayerList*/().playerEntityList/*was:players*/;
        HashSet<Player> reference = new HashSet<Player>(players.size());
        for (net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/ player : players) {
            reference.add(CraftPlayerCache.getCraftPlayer(player));
        }
        return reference;
    }

}
