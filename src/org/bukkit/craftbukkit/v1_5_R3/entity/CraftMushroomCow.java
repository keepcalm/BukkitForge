package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.passive.EntityMooshroom;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftMushroomCow extends CraftCow implements MushroomCow {
    public CraftMushroomCow(CraftServer server, EntityMooshroom entity) {
        super(server, entity);
    }

    @Override
    public EntityMooshroom getHandle() {
        return (EntityMooshroom) entity;
    }

    @Override
    public String toString() {
        return "CraftMushroomCow";
    }

    public EntityType getType() {
        return EntityType.MUSHROOM_COW;
    }
}
