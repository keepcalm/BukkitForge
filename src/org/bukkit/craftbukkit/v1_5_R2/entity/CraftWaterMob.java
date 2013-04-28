package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.passive.EntityWaterMob;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.WaterMob;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftWaterMob extends CraftCreature implements WaterMob {

    public CraftWaterMob(CraftServer server, EntityWaterMob entity) {
        super(server, entity);
    }

    @Override
    public EntityWaterMob getHandle() {
        return (EntityWaterMob) entity;
    }

    @Override
    public String toString() {
        return "CraftWaterMob";
    }
}
