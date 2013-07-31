package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.projectile.EntityArrow;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

public class CraftArrow extends AbstractProjectile implements Arrow {

    public CraftArrow(CraftServer server, EntityArrow entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().shootingEntity != null) {
            return (LivingEntity) CraftEntity.getEntity(this.server, getHandle().shootingEntity);
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().shootingEntity = ((CraftLivingEntity) shooter).getHandle();
        }
    }

    @Override
    public EntityArrow getHandle() {
        return (EntityArrow) entity;
    }

    @Override
    public String toString() {
        return "CraftArrow";
    }

    public EntityType getType() {
        return EntityType.ARROW;
    }
}
