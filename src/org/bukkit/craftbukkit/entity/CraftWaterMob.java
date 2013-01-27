package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityWaterMob;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.WaterMob;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftWaterMob extends BukkitCreature implements WaterMob {

    public CraftWaterMob(BukkitServer server, EntityWaterMob entity) {
        super(server, entity);
    }

    @Override
    public EntityWaterMob getHandle() {
        return (EntityWaterMob) entity;
    }

    @Override
    public String toString() {
        return "BukkitWaterMob";
    }
}
