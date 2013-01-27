package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftFish extends AbstractProjectile implements Fish {
    public CraftFish(CraftServer server, EntityFishHook entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().angler != null) {
            return (LivingEntity) CraftFish.getEntity(server, getHandle().angler);
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof CraftEntityHuman) {
            getHandle().angler = (EntityPlayer) ((CraftEntityHuman) shooter).entity;
        }
    }

    @Override
    public EntityFishHook getHandle() {
        return (EntityFishHook) entity;
    }

    @Override
    public String toString() {
        return "CraftFish";
    }

    public EntityType getType() {
        return EntityType.FISHING_HOOK;
    }
}
