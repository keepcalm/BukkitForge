package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntitySquid;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftSquid extends BukkitWaterMob implements Squid {

    public CraftSquid(BukkitServer server, EntitySquid entity) {
        super(server, entity);
    }

    @Override
    public EntitySquid getHandle() {
        return (EntitySquid) entity;
    }

    @Override
    public String toString() {
        return "BukkitSquid";
    }

    public EntityType getType() {
        return EntityType.SQUID;
    }
}
