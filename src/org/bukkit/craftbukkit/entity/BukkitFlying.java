package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.EntityFlying;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Flying;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitFlying extends BukkitLivingEntity implements Flying {

    public BukkitFlying(BukkitServer server, EntityFlying entity) {
        super(server, entity);
    }

    @Override
    public EntityFlying getHandle() {
        return (EntityFlying) entity;
    }

    @Override
    public String toString() {
        return "BukkitFlying";
    }
}
