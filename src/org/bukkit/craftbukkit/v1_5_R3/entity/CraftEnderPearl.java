package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.item.EntityEnderPearl;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftEnderPearl extends CraftProjectile implements EnderPearl {
    public CraftEnderPearl(CraftServer server, EntityEnderPearl entity) {
        super(server, entity);
    }

    @Override
    public EntityEnderPearl getHandle() {
        return (EntityEnderPearl) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderPearl";
    }

    public EntityType getType() {
        return EntityType.ENDER_PEARL;
    }
}
