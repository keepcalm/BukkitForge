package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.projectile.EntityArrow;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftArrow extends AbstractProjectile implements Arrow {

    public CraftArrow(BukkitServer server, EntityArrow entity) {
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
            getHandle().shootingEntity = ((BukkitLivingEntity) shooter).getHandle();
        }
    }

    @Override
    public EntityArrow getHandle() {
        return (EntityArrow) entity;
    }

    @Override
    public String toString() {
        return "BukkitArrow";
    }

    public EntityType getType() {
        return EntityType.ARROW;
    }
}
