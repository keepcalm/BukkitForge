package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityWither;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;

public class BukkitWither extends BukkitMonster implements Wither {
    public BukkitWither(BukkitServer server, EntityWither entity) {
        super(server, entity);
    }

    @Override
    public EntityWither getHandle() {
        return (EntityWither) entity;
    }

    @Override
    public String toString() {
        return "BukkitWither";
    }

    public EntityType getType() {
        return EntityType.WITHER;
    }
}
