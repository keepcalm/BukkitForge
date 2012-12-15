package keepcalm.mods.bukkit.bukkitAPI.entity;

//import net.minecraft.src.EntityEnderSignal;
//import org.bukkit.craftbukkit.CraftServer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

import net.minecraft.entity.Entity;

import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
/**
 * <b>WARNING</b>: This is just a compatibility class!!! This is never used.
 * 
 * @deprecated
 * @author keepcalm
 * 
 *
 */
public class BukkitEnderSignal extends BukkitEntity implements EnderSignal {
    public BukkitEnderSignal(BukkitServer server, Entity entity) {
        super(server, entity);
    }

    @Override
    public Entity getHandle() {
        return (Entity) entity;
    }

    @Override
    public String toString() {
        return "BukkitEnderSignal";
    }

    public EntityType getType() {
        return EntityType.ENDER_SIGNAL;
    }
}