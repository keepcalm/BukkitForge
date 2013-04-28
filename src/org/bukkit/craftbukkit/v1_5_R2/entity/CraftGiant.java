package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.monster.EntityGiantZombie;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftGiant extends CraftMonster implements Giant {

    public CraftGiant(CraftServer server, EntityGiantZombie entity) {
        super(server, entity);
    }

    @Override
    public EntityGiantZombie getHandle() {
        return (EntityGiantZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftGiant";
    }

    public EntityType getType() {
        return EntityType.GIANT;
    }
}
