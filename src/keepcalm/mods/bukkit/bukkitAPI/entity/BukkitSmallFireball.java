package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.projectile.EntitySmallFireball;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.SmallFireball;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitSmallFireball extends BukkitFireball implements SmallFireball {
    public BukkitSmallFireball(BukkitServer server, EntitySmallFireball entity) {
        super(server, entity);
    }

    @Override
    public EntitySmallFireball getHandle() {
        return (EntitySmallFireball) entity;
    }

    @Override
    public String toString() {
        return "BukkitSmallFireball";
    }

    public EntityType getType() {
        return EntityType.SMALL_FIREBALL;
    }
}
