package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntityMob;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Monster;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftMonster extends BukkitCreature implements Monster {

    public CraftMonster(BukkitServer server, EntityMob entity) {
        super(server, entity);
    }

    @Override
    public EntityMob getHandle() {
        return (EntityMob) entity;
    }

    @Override
    public String toString() {
        return "BukkitMonster";
    }
}
