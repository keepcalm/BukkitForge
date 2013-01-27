package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Vehicle;
//import org.bukkit.craftbukkit.CraftServer;

public abstract class CraftVehicle extends BukkitEntity implements Vehicle {
    public CraftVehicle(BukkitServer server, net.minecraft.entity.Entity entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "BukkitVehicle{passenger=" + getPassenger() + '}';
    }
}
