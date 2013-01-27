package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.projectile.EntitySnowball;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftSnowball extends BukkitProjectile implements Snowball {
    public CraftSnowball(BukkitServer server, EntitySnowball entity) {
        super(server, entity);
    }

    @Override
    public EntitySnowball getHandle() {
        return (EntitySnowball) entity;
    }

    @Override
    public String toString() {
        return "BukkitSnowball";
    }

    public EntityType getType() {
        return EntityType.SNOWBALL;
    }
}
