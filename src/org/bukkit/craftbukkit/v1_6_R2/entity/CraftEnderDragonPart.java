package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.boss.EntityDragonPart;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.util.NumberConversions;

public class CraftEnderDragonPart extends CraftComplexPart implements EnderDragonPart {
    public CraftEnderDragonPart(CraftServer server, EntityDragonPart entity) {
        super(server, entity);
    }

    @Override
    public EnderDragon getParent() {
        return (EnderDragon) super.getParent();
    }

    @Override
    public EntityDragonPart getHandle() {
        return (EntityDragonPart) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderDragonPart";
    }

	@Override
	public void damage(double amount) {
        getParent().damage(amount);
    }

    public void damage(double amount, Entity source) {
        getParent().damage(amount, source);
    }

	public double getHealth() {
        return getParent().getHealth();
    }

    public void setHealth(double health) {
        getParent().setHealth(health);
    }

    public double getMaxHealth() {
        return getParent().getMaxHealth();
    }

    public void setMaxHealth(double health) {
        getParent().setMaxHealth(health);
    }

    public void resetMaxHealth() {
        getParent().resetMaxHealth();
    }
}
