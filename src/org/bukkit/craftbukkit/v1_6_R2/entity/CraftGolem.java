package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.monster.EntityGolem;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.Golem;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

public class CraftGolem extends CraftCreature implements Golem {
    public CraftGolem(CraftServer server, EntityGolem entity) {
        super(server, entity);
    }

    @Override
    public EntityGolem getHandle() {
        return (EntityGolem) entity;
    }

    @Override
    public String toString() {
        return "CraftGolem";
    }
}
