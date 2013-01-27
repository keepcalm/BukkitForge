package org.bukkit.craftbukkit.entity;

//import net.minecraft.src.EntityEnderSignal;
//import org.bukkit.craftbukkit.CraftServer;
import net.minecraft.entity.Entity;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
/**
 * <b>WARNING</b>: This is just a compatibility class!!! This is never used.
 * 
 * @deprecated
 * @author keepcalm
 * 
 *
 */
public class BukkitEnderSignal extends BukkitEntity implements EnderSignal {
    public BukkitEnderSignal(BukkitServer server, Entity entity) {
        super(server, entity);
    }

    @Override
    public Entity getHandle() {
        return (Entity) entity;
    }

    @Override
    public String toString() {
        return "BukkitEnderSignal";
    }

    public EntityType getType() {
        return EntityType.ENDER_SIGNAL;
    }
}