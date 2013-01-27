package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.projectile.EntityEgg;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftEgg extends BukkitProjectile implements Egg {
    public CraftEgg(BukkitServer server, EntityEgg entity) {
        super(server, entity);
    }

    @Override
    public EntityEgg getHandle() {
        return (EntityEgg) entity;
    }

    @Override
    public String toString() {
        return "BukkitEgg";
    }

    public EntityType getType() {
        return EntityType.EGG;
    }
}
