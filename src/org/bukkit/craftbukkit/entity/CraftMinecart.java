package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityMinecart;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;
//import org.bukkit.craftbukkit.CraftServer;
/**
 * 
 * @author keepcalm
 * @author CraftCraft
 *
 */
public class CraftMinecart extends CraftVehicle implements Minecart {
    /**
     * Stores the minecart type id, which is used by Minecraft to differentiate
     * minecart types. Here we use subclasses.
     */
    public enum Type {
        Minecart(0),
        StorageMinecart(1),
        PoweredMinecart(2);

        private final int id;

        private Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
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
        return getHandle().getMaxSpeedRail();
    }

    public void setMaxSpeed(double speed) {
        if (speed >= 0D) {
            getHandle().setMaxSpeedRail((float)speed);
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

    @Override
    public String toString() {
        return "CraftMinecart";
    }

    public EntityType getType() {
        return EntityType.MINECART;
    }
}
