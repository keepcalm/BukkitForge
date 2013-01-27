package org.bukkit.craftbukkit.utils;

import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.craftbukkit.entity.BukkitEntity;
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
            reference.add((Player) BukkitEntity.getEntity((BukkitServer) Bukkit.getServer(), player));
        }
        return reference;
    }

}
