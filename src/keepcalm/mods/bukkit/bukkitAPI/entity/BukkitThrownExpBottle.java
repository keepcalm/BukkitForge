package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityExpBottle;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownExpBottle;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitThrownExpBottle extends BukkitProjectile implements ThrownExpBottle {
    public BukkitThrownExpBottle(BukkitServer server, EntityExpBottle entity) {
        super(server, entity);
    }

    @Override
    public EntityExpBottle getHandle() {
        return (EntityExpBottle) entity;
    }

    @Override
    public String toString() {
        return "BukkitThrownExpBottle";
    }

    public EntityType getType() {
        return EntityType.THROWN_EXP_BOTTLE;
    }
}
