package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.monster.EntityBlaze;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftBlaze extends CraftMonster implements Blaze {
    public CraftBlaze(CraftServer server, EntityBlaze entity) {
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