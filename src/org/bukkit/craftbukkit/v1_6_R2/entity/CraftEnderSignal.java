package org.bukkit.craftbukkit.v1_6_R2.entity;

//import net.minecraft.src.EntityEnderSignal;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import net.minecraft.entity.Entity;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
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
public class CraftEnderSignal extends CraftEntity implements EnderSignal {
    public CraftEnderSignal(CraftServer server, Entity entity) {
        super(server, entity);
    }

    @Override
    public Entity getHandle() {
        return (Entity) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderSignal";
    }

    public EntityType getType() {
        return EntityType.ENDER_SIGNAL;
    }
}