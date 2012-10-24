package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityMob;

//import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Monster;

public class BukkitMonster extends BukkitCreature implements Monster {

    public BukkitMonster(BukkitServer server, EntityMob entity) {
        super(server, entity);
    }

    @Override
    public EntityMob getHandle() {
        return (EntityMob) entity;
    }

    @Override
    public String toString() {
        return "BukkitMonster";
    }
}
