package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityThrowable;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
//import org.bukkit.craftbukkit.CraftServer;

public abstract class BukkitProjectile extends AbstractProjectile implements Projectile {
    public BukkitProjectile(BukkitServer server, net.minecraft.entity.Entity entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().thrower instanceof EntityLiving) {
            return (LivingEntity) this.getEntity(this.server, getHandle().thrower);
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof BukkitLivingEntity) {
            getHandle().thrower = (EntityLiving) ((BukkitLivingEntity) shooter).entity;
        }
    }

    @Override
    public EntityThrowable getHandle() {
        return (EntityThrowable) entity;
    }

    @Override
    public String toString() {
        return "BukkitProjectile";
    }
    
    
}
