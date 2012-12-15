package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.monster.EntitySkeleton;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitSkeleton extends BukkitMonster implements Skeleton {

    public BukkitSkeleton(BukkitServer server, EntitySkeleton entity) {
        super(server, entity);
    }

    @Override
    public EntitySkeleton getHandle() {
        return (EntitySkeleton) entity;
    }

    @Override
    public String toString() {
        return "CraftSkeleton";
    }

    public EntityType getType() {
        return EntityType.SKELETON;
    }
}
