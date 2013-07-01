package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.monster.EntitySpider;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftSpider extends CraftMonster implements Spider {

    public CraftSpider(CraftServer server, EntitySpider entity) {
        super(server, entity);
    }

    @Override
    public EntitySpider getHandle() {
        return (EntitySpider) entity;
    }

    @Override
    public String toString() {
        return "CraftSpider";
    }

    public EntityType getType() {
        return EntityType.SPIDER;
    }
}
