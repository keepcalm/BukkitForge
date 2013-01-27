package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityAmbientCreature;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.EntityType;

public class BukkitAmbient extends BukkitLivingEntity implements Ambient {
    public BukkitAmbient(BukkitServer server, EntityAmbientCreature entity) {
        super(server, entity);
    }

    @Override
    public EntityAmbientCreature getHandle() {
        return (EntityAmbientCreature) entity;
    }

    @Override
    public String toString() {
        return "BukkitAmbient";
    }

    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
}
