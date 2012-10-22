package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityArrow;

//import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class BukkitArrow extends AbstractProjectile implements Arrow {

    public BukkitArrow(BukkitServer server, EntityArrow entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().shootingEntity != null) {
            return (LivingEntity) BukkitEntity.getEntity(this.server, getHandle().shootingEntity);
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof BukkitLivingEntity) {
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
