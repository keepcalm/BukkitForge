package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityGolem;
//import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Golem;

public class BukkitGolem extends BukkitCreature implements Golem {
    public BukkitGolem(BukkitServer server, EntityGolem entity) {
        super(server, entity);
    }

    @Override
    public EntityGolem getHandle() {
        return (EntityGolem) entity;
    }

    @Override
    public String toString() {
        return "BukkitGolem";
    }
}
