package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntityBlaze;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftBlaze extends BukkitMonster implements Blaze {
    public CraftBlaze(BukkitServer server, EntityBlaze entity) {
        super(server, entity);
    }

    @Override
    public EntityBlaze getHandle() {
        return (EntityBlaze) entity;
    }

    @Override
    public String toString() {
        return "CraftBlaze";
    }

    public EntityType getType() {
        return EntityType.BLAZE;
    }
}
