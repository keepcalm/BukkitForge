package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityEnderPearl;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitEnderPearl extends BukkitProjectile implements EnderPearl {
    public BukkitEnderPearl(BukkitServer server, EntityEnderPearl entity) {
        super(server, entity);
    }

    @Override
    public EntityEnderPearl getHandle() {
        return (EntityEnderPearl) entity;
    }

    @Override
    public String toString() {
        return "BukkitEnderPearl";
    }

    public EntityType getType() {
        return EntityType.ENDER_PEARL;
    }
}
