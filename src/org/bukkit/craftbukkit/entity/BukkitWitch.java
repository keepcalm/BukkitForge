package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntityWitch;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

public class BukkitWitch extends BukkitMonster implements Witch {
    public BukkitWitch(BukkitServer server, EntityWitch entity) {
        super(server, entity);
    }

    @Override
    public EntityWitch getHandle() {
        return (EntityWitch) entity;
    }

    @Override
    public String toString() {
        return "BukkitWitch";
    }

    public EntityType getType() {
        return EntityType.WITCH;
    }
}
