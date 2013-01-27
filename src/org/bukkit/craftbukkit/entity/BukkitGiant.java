package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntityGiantZombie;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitGiant extends BukkitMonster implements Giant {

    public BukkitGiant(BukkitServer server, EntityGiantZombie entity) {
        super(server, entity);
    }

    @Override
    public EntityGiantZombie getHandle() {
        return (EntityGiantZombie) entity;
    }

    @Override
    public String toString() {
        return "BukkitGiant";
    }

    public EntityType getType() {
        return EntityType.GIANT;
    }
}
