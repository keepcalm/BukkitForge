package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.projectile.EntityEgg;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftEgg extends CraftProjectile implements Egg {
    public CraftEgg(CraftServer server, EntityEgg entity) {
        super(server, entity);
    }

    @Override
    public EntityEgg getHandle() {
        return (EntityEgg) entity;
    }

    @Override
    public String toString() {
        return "CraftEgg";
    }

    public EntityType getType() {
        return EntityType.EGG;
    }
}
