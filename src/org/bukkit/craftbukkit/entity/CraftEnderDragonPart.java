package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.boss.EntityDragonPart;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
//import org.bukkit.craftbukkit.CraftServer;

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
	public void damage(int amount) {
		getParent().damage(amount);
	}

	@Override
	public void damage(int amount, Entity source) {
		getParent().damage(amount, source);
	}

	@Override
	public int getHealth() {
		return getParent().getHealth();
	}

	@Override
	public void setHealth(int health) {
		getParent().setHealth(health);
	}

	@Override
	public int getMaxHealth() {
		return getParent().getMaxHealth();
	}

	@Override
	public void setMaxHealth(int health) {
		getParent().setMaxHealth(health);
	}

	@Override
	public void resetMaxHealth() {
		getParent().resetMaxHealth();
	}
}
