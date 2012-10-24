package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityMooshroom;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitMushroomCow extends BukkitCow implements MushroomCow {
    public BukkitMushroomCow(BukkitServer server, EntityMooshroom entity) {
        super(server, entity);
    }

    @Override
    public EntityMooshroom getHandle() {
        return (EntityMooshroom) entity;
    }

    @Override
    public String toString() {
        return "BukkitMushroomCow";
    }

    public EntityType getType() {
        return EntityType.MUSHROOM_COW;
    }
}
