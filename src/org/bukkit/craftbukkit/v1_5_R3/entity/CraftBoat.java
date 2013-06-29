package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.item.EntityBoat;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftBoat extends CraftVehicle implements Boat {
	private double speed = 0.2;
	private double emptySpeed = -1;
    public CraftBoat(CraftServer server, EntityBoat entity) {
        super(server, entity);
    }

    public double getMaxSpeed() {
        return getHandle().motionX + 40;
    }

    public void setMaxSpeed(double speed) {
        if (speed >= 0D) {
            //getHandle().maxSpeed = speed;
        }
    }

    public double getOccupiedDeceleration() {
        return speed;
    }

    public void setOccupiedDeceleration(double speed) {
        if (speed >= 0D) {
            this.speed = speed;
        }
    }

    public double getUnoccupiedDeceleration() {
        return emptySpeed;
    }

    public void setUnoccupiedDeceleration(double speed) {
        this.emptySpeed = speed;
    }

    public boolean getWorkOnLand() {
        return false;
    }

    public void setWorkOnLand(boolean workOnLand) {
        //getHandle().landBoats = workOnLand;
    	// FIXME: UNIMPLEMENTED
    }

    @Override
    public EntityBoat getHandle() {
        return (EntityBoat) entity;
    }

    @Override
    public String toString() {
        return "CraftBoat";
    }

    public EntityType getType() {
        return EntityType.BOAT;
    }
}
