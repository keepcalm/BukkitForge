package org.bukkit.util;

import java.util.HashSet;
import java.util.List;

import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
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
            reference.add(BukkitForgePlayerCache.getCraftPlayer(player));
        }
        return reference;
    }

}
