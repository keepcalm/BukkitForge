package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.monster.EntitySilverfish;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftSilverfish extends CraftMonster implements Silverfish {
    public CraftSilverfish(CraftServer server, EntitySilverfish entity) {
        super(server, entity);
    }

    @Override
    public EntitySilverfish getHandle() {
        return (EntitySilverfish) entity;
    }

    @Override
    public String toString() {
        return "CraftSilverfish";
    }

    public EntityType getType() {
        return EntityType.SILVERFISH;
    }
}
