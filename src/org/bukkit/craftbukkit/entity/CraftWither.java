package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.boss.EntityWither;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;

public class CraftWither extends BukkitMonster implements Wither {
    public CraftWither(BukkitServer server, EntityWither entity) {
        super(server, entity);
    }

    @Override
    public EntityWither getHandle() {
        return (EntityWither) entity;
    }

    @Override
    public String toString() {
        return "BukkitWither";
    }

    public EntityType getType() {
        return EntityType.WITHER;
    }
}
