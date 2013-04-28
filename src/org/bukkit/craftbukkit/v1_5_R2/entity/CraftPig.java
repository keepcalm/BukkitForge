package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.passive.EntityPig;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftPig extends CraftAnimals implements Pig {
    public CraftPig(CraftServer server, EntityPig entity) {
        super(server, entity);
    }

    public boolean hasSaddle() {
        return getHandle().getSaddled();
    }

    public void setSaddle(boolean saddled) {
        getHandle().setSaddled(saddled);
    }

    public EntityPig getHandle() {
        return (EntityPig) entity;
    }

    @Override
    public String toString() {
        return "CraftPig";
    }

    public EntityType getType() {
        return EntityType.PIG;
    }
}
