package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.monster.EntityMob;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.Monster;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

public class CraftMonster extends CraftCreature implements Monster {

    public CraftMonster(CraftServer server, EntityMob entity) {
        super(server, entity);
    }

    @Override
    public EntityMob getHandle() {
        return (EntityMob) entity;
    }

    @Override
    public String toString() {
        return "CraftMonster";
    }
}
