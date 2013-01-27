package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityBat;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

public class BukkitBat extends BukkitAmbient implements Bat {
    public BukkitBat(BukkitServer server, EntityBat entity) {
        super(server, entity);
    }

    @Override
    public EntityBat getHandle() {
        return (EntityBat) entity;
    }

    @Override
    public String toString() {
        return "BukkitBat";
    }

    public EntityType getType() {
        return EntityType.BAT;
    }
}
