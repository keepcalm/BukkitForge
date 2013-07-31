package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.monster.EntityCaveSpider;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

public class CraftCaveSpider extends CraftSpider implements CaveSpider {
    public CraftCaveSpider(CraftServer server, EntityCaveSpider entity) {
        super(server, entity);
    }

    @Override
    public EntityCaveSpider getHandle() {
        return (EntityCaveSpider) entity;
    }

    @Override
    public String toString() {
        return "CraftCaveSpider";
    }

    public EntityType getType() {
        return EntityType.CAVE_SPIDER;
    }
}
