package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.monster.EntityZombie;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftZombie extends CraftMonster implements Zombie {

    public CraftZombie(CraftServer server, EntityZombie entity) {
        super(server, entity);
    }

    @Override
    public EntityZombie getHandle() {
        return (EntityZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftZombie";
    }

    public EntityType getType() {
        return EntityType.ZOMBIE;
    }

	@Override
	public boolean isBaby() {
		return getHandle().isChild();
	}

	@Override
	public void setBaby(boolean flag) {
		 getHandle().setChild(true);
	}

	@Override
	public boolean isVillager() {
		return getHandle().isVillager();
	}

	@Override
	public void setVillager(boolean flag) {
		getHandle().setVillager(true);
	}
}
