package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftFish extends AbstractProjectile implements Fish {
	
	private double biteChance = -1;
	
    public CraftFish(CraftServer server, EntityFishHook entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().angler != null) {
            return (LivingEntity) CraftFish.getEntity(server, getHandle().angler);
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof CraftHumanEntity) {
            getHandle().angler = (EntityPlayer) ((CraftHumanEntity) shooter).entity;
        }
    }

    @Override
    public EntityFishHook getHandle() {
        return (EntityFishHook) entity;
    }

    @Override
    public String toString() {
        return "CraftFish";
    }

    public EntityType getType() {
        return EntityType.FISHING_HOOK;
    }
    
    public double getBiteChance() {
        net.minecraft.entity.projectile.EntityFishHook hook = getHandle();

        if (this.biteChance == -1) {
            if (hook.worldObj.canLightningStrikeAt(net.minecraft.util.MathHelper.floor_double(hook.posX), net.minecraft.util.MathHelper.floor_double(hook.posY) + 1, net.minecraft.util.MathHelper.floor_double(hook.posZ))) {
                return 1/300.0;
            }
            return 1/500.0;
        }
        return this.biteChance;
    }

    public void setBiteChance(double chance) {
        Validate.isTrue(chance >= 0 && chance <= 1, "The bite chance must be between 0 and 1.");
        this.biteChance = chance;
    }
}
