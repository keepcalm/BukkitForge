package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.projectile.EntityEgg;

import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitEgg extends BukkitProjectile implements Egg {
    public BukkitEgg(BukkitServer server, EntityEgg entity) {
        super(server, entity);
    }

    @Override
    public EntityEgg getHandle() {
        return (EntityEgg) entity;
    }

    @Override
    public String toString() {
        return "BukkitEgg";
    }

    public EntityType getType() {
        return EntityType.EGG;
    }
}
