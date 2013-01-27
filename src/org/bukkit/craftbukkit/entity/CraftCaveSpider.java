package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntityCaveSpider;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftCaveSpider extends BukkitSpider implements CaveSpider {
    public CraftCaveSpider(BukkitServer server, EntityCaveSpider entity) {
        super(server, entity);
    }

    @Override
    public EntityCaveSpider getHandle() {
        return (EntityCaveSpider) entity;
    }

    @Override
    public String toString() {
        return "BukkitCaveSpider";
    }

    public EntityType getType() {
        return EntityType.CAVE_SPIDER;
    }
}
