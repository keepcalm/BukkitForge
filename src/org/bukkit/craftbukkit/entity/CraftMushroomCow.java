package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityMooshroom;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftMushroomCow extends BukkitCow implements MushroomCow {
    public CraftMushroomCow(BukkitServer server, EntityMooshroom entity) {
        super(server, entity);
    }

    @Override
    public EntityMooshroom getHandle() {
        return (EntityMooshroom) entity;
    }

    @Override
    public String toString() {
        return "BukkitMushroomCow";
    }

    public EntityType getType() {
        return EntityType.MUSHROOM_COW;
    }
}
