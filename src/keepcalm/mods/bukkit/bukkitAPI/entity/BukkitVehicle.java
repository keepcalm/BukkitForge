package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

import org.bukkit.entity.Vehicle;
//import org.bukkit.craftbukkit.CraftServer;

public abstract class BukkitVehicle extends BukkitEntity implements Vehicle {
    public BukkitVehicle(BukkitServer server, net.minecraft.entity.Entity entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "BukkitVehicle{passenger=" + getPassenger() + '}';
    }
}
