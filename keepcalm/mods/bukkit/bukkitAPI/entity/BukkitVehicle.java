package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

//import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Vehicle;

public abstract class BukkitVehicle extends BukkitEntity implements Vehicle {
    public BukkitVehicle(BukkitServer server, net.minecraft.src.Entity entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "BukkitVehicle{passenger=" + getPassenger() + '}';
    }
}
