package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.item.EntityExpBottle;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownExpBottle;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftThrownExpBottle extends CraftProjectile implements ThrownExpBottle {
    public CraftThrownExpBottle(CraftServer server, EntityExpBottle entity) {
        super(server, entity);
    }

    @Override
    public EntityExpBottle getHandle() {
        return (EntityExpBottle) entity;
    }

    @Override
    public String toString() {
        return "CraftThrownExpBottle";
    }

    public EntityType getType() {
        return EntityType.THROWN_EXP_BOTTLE;
    }
}
