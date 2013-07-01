package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.item.EntityFallingSand;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftFallingSand extends CraftEntity implements FallingBlock {
	private boolean drop = true;
	
    public CraftFallingSand(CraftServer server, EntityFallingSand entity) {
        super(server, entity);
    }

    @Override
    public EntityFallingSand getHandle() {
        return (EntityFallingSand) entity;
    }

    @Override
    public String toString() {
        return "CraftFallingSand";
    }

    public EntityType getType() {
        return EntityType.FALLING_BLOCK;
    }

    public Material getMaterial() {
        return Material.getMaterial(getBlockId());
    }

    public int getBlockId() {
        return getHandle().blockID;
    }

    public byte getBlockData() {
    	// the block data - FIXME
        return (byte) 0;
    }

    public boolean getDropItem() {
        return drop;
    }

    public void setDropItem(boolean drop) {
        this.drop = drop;
    }
}
