package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntitySnowman;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftSnowman extends BukkitGolem implements Snowman {
    public CraftSnowman(BukkitServer server, EntitySnowman entity) {
        super(server, entity);
    }

    @Override
    public EntitySnowman getHandle() {
        return (EntitySnowman) entity;
    }

    @Override
    public String toString() {
        return "BukkitSnowman";
    }

    public EntityType getType() {
        return EntityType.SNOWMAN;
    }
}
