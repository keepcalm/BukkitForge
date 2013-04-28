package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.item.EntityMinecart;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

public abstract class CraftMinecart extends CraftVehicle implements Minecart {
	private Vector minecartVelocityDerailed = new Vector(1,1,1);
    private Vector minecartVelocityAir = new Vector(1,1,1);
    private boolean slowWhenEmpty = true;
    
    public CraftMinecart(CraftServer server, EntityMinecart entity) {
        super(server, entity);
    }

    public void setDamage(int damage) {
        getHandle().setDamage(damage);
    }

    public int getDamage() {
        return getHandle().getDamage();
    }

    public double getMaxSpeed() {
        return getHandle().getCurrentCartSpeedCapOnRail();
    }

    public void setMaxSpeed(double speed) {
        if (speed >= 0D) {
        	getHandle().setCurrentCartSpeedCapOnRail((float)speed);
        }
    }

    public boolean isSlowWhenEmpty() {
        return slowWhenEmpty;
    }

    public void setSlowWhenEmpty(boolean slow) {
        this.slowWhenEmpty = slow;
    }

    public Vector getFlyingVelocityMod() {
        return this.minecartVelocityAir;
    }

    public void setFlyingVelocityMod(Vector flying) {
        this.minecartVelocityAir = flying;
    }

    public Vector getDerailedVelocityMod() {
        //return getHandle().getDerailedVelocityMod();
    	return this.minecartVelocityDerailed;
    }

    public void setDerailedVelocityMod(Vector derailed) {
        //getHandle().(derailed);
    	this.minecartVelocityDerailed = derailed;
    }

    @Override
    public EntityMinecart getHandle() {
        return (EntityMinecart) entity;
    }
}
