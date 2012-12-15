package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.monster.EntityBlaze;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitBlaze extends BukkitMonster implements Blaze {
    public BukkitBlaze(BukkitServer server, EntityBlaze entity) {
        super(server, entity);
    }

    @Override
    public EntityBlaze getHandle() {
        return (EntityBlaze) entity;
    }

    @Override
    public String toString() {
        return "CraftBlaze";
    }

    public EntityType getType() {
        return EntityType.BLAZE;
    }
}
