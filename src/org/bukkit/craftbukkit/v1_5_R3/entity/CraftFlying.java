package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.EntityFlying;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.Flying;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftFlying extends CraftLivingEntity implements Flying {

    public CraftFlying(CraftServer server, EntityFlying entity) {
        super(server, entity);
    }

    @Override
    public EntityFlying getHandle() {
        return (EntityFlying) entity;
    }

    @Override
    public String toString() {
        return "CraftFlying";
    }
}
