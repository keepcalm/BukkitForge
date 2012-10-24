package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityChicken;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitChicken extends BukkitAnimals implements Chicken {

    public BukkitChicken(BukkitServer server, EntityChicken entity) {
        super(server, entity);
    }

    @Override
    public EntityChicken getHandle() {
        return (EntityChicken) entity;
    }

    @Override
    public String toString() {
        return "CraftChicken";
    }

    public EntityType getType() {
        return EntityType.CHICKEN;
    }
}
