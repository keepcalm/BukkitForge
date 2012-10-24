package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityAnimal;
//import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Animals;

public class BukkitAnimals extends BukkitAgeable implements Animals {

    public BukkitAnimals(BukkitServer server, EntityAnimal entity) {
        super(server, entity);
    }

    @Override
    public EntityAnimal getHandle() {
        return (EntityAnimal) entity;
    }

    @Override
    public String toString() {
        return "BukkitAnimals";
    }
}
