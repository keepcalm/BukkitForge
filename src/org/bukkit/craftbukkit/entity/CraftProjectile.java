package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityThrowable;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
//import org.bukkit.craftbukkit.CraftServer;

public abstract class CraftProjectile extends AbstractProjectile implements Projectile {
    public CraftProjectile(CraftServer server, net.minecraft.entity.Entity entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().getThrower() instanceof EntityLiving) {
            return (LivingEntity) CraftProjectile.getEntity(this.server, getHandle().getThrower());
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().thrower = (EntityLiving) ((CraftLivingEntity) shooter).entity;
        }
    }

    @Override
    public EntityThrowable getHandle() {
        return (EntityThrowable) entity;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }
    
    
}
