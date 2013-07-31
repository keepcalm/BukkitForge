package org.bukkit.craftbukkit.v1_6_R2.utils;

import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.entity.Player;

public class LazyPlayerSet extends LazyHashSet<Player> {

    @Override
    HashSet<Player> makeReference() {
        if (reference != null) {
            throw new IllegalStateException("Reference already created!");
        }
        List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        HashSet<Player> reference = new HashSet<Player>(players.size());
        for (EntityPlayerMP player : players) {
            reference.add((Player) CraftEntity.getEntity((CraftServer) Bukkit.getServer(), player));
        }
        return reference;
    }

}
