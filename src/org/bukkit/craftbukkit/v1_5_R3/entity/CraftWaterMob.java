package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.passive.EntityWaterMob;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.WaterMob;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

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
