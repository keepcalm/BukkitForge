package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntitySilverfish;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitSilverfish extends BukkitMonster implements Silverfish {
    public BukkitSilverfish(BukkitServer server, EntitySilverfish entity) {
        super(server, entity);
    }

    @Override
    public EntitySilverfish getHandle() {
        return (EntitySilverfish) entity;
    }

    @Override
    public String toString() {
        return "BukkitSilverfish";
    }

    public EntityType getType() {
        return EntityType.SILVERFISH;
    }
}
