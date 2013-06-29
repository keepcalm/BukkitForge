package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.passive.EntityCow;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftCow extends CraftAnimals implements Cow {

    public CraftCow(CraftServer server, EntityCow entity) {
        super(server, entity);
    }

    @Override
    public EntityCow getHandle() {
        return (EntityCow) entity;
    }

    @Override
    public String toString() {
        return "CraftCow";
    }

    public EntityType getType() {
        return EntityType.COW;
    }
}
