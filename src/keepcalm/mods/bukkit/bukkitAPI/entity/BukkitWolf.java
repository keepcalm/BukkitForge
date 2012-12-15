package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.passive.EntityWolf;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitWolf extends BukkitTameableAnimal implements Wolf {
    public BukkitWolf(BukkitServer server, EntityWolf wolf) {
        super(server, wolf);
    }

    public boolean isAngry() {
        return getHandle().isAngry();
    }

    public void setAngry(boolean angry) {
        getHandle().setAngry(angry);
    }

    @Override
    public EntityWolf getHandle() {
        return (EntityWolf) entity;
    }

    @Override
    public EntityType getType() {
        return EntityType.WOLF;
    }
}
