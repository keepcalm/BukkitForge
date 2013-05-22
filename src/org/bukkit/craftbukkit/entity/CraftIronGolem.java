package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntityIronGolem;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftIronGolem extends CraftGolem implements IronGolem {
	private boolean playerCreated = false;
    public CraftIronGolem(CraftServer server, EntityIronGolem entity) {
        super(server, entity);
    }

    @Override
    public EntityIronGolem getHandle() {
        return (EntityIronGolem) entity;
    }

    @Override
    public String toString() {
        return "CraftIronGolem";
    }

    public boolean isPlayerCreated() {
    	// FIXME: Stub
        return playerCreated;
    }

    public void setPlayerCreated(boolean playerCreated) {
    	// FIXME: Stub
        this.playerCreated = playerCreated;
    }

    @Override
    public EntityType getType() {
        return EntityType.IRON_GOLEM;
    }
}
