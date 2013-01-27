package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityPig;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitPig extends BukkitAnimals implements Pig {
    public BukkitPig(BukkitServer server, EntityPig entity) {
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
        return "BukkitPig";
    }

    public EntityType getType() {
        return EntityType.PIG;
    }
}
