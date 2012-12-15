package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.monster.EntityZombie;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitZombie extends BukkitMonster implements Zombie {

    public BukkitZombie(BukkitServer server, EntityZombie entity) {
        super(server, entity);
    }

    @Override
    public EntityZombie getHandle() {
        return (EntityZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftZombie";
    }

    public EntityType getType() {
        return EntityType.ZOMBIE;
    }
}
