package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.monster.EntityGhast;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitGhast extends BukkitFlying implements Ghast {

    public BukkitGhast(BukkitServer server, EntityGhast entity) {
        super(server, entity);
    }

    @Override
    public EntityGhast getHandle() {
        return (EntityGhast) entity;
    }

    @Override
    public String toString() {
        return "BukkitGhast";
    }

    public EntityType getType() {
        return EntityType.GHAST;
    }
}
