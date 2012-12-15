package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.monster.EntityMagmaCube;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitMagmaCube extends BukkitSlime implements MagmaCube {

    public BukkitMagmaCube(BukkitServer server, EntityMagmaCube entity) {
        super(server, entity);
    }

    public EntityMagmaCube getHandle() {
        return (EntityMagmaCube) entity;
    }

    @Override
    public String toString() {
        return "BukkitMagmaCube";
    }

    public EntityType getType() {
        return EntityType.MAGMA_CUBE;
    }
}
