package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityCow;

import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitCow extends BukkitAnimals implements Cow {

    public BukkitCow(BukkitServer server, EntityCow entity) {
        super(server, entity);
    }

    @Override
    public EntityCow getHandle() {
        return (EntityCow) entity;
    }

    @Override
    public String toString() {
        return "BukkitCow";
    }

    public EntityType getType() {
        return EntityType.COW;
    }
}
