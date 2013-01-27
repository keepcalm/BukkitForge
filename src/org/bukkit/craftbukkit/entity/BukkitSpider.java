package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntitySpider;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitSpider extends BukkitMonster implements Spider {

    public BukkitSpider(BukkitServer server, EntitySpider entity) {
        super(server, entity);
    }

    @Override
    public EntitySpider getHandle() {
        return (EntitySpider) entity;
    }

    @Override
    public String toString() {
        return "BukkitSpider";
    }

    public EntityType getType() {
        return EntityType.SPIDER;
    }
}
