package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntitySquid;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitSquid extends BukkitWaterMob implements Squid {

    public BukkitSquid(BukkitServer server, EntitySquid entity) {
        super(server, entity);
    }

    @Override
    public EntitySquid getHandle() {
        return (EntitySquid) entity;
    }

    @Override
    public String toString() {
        return "BukkitSquid";
    }

    public EntityType getType() {
        return EntityType.SQUID;
    }
}
