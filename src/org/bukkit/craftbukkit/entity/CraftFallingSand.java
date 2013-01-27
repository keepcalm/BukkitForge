package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityFallingSand;

import org.bukkit.Material;
import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftFallingSand extends BukkitEntity implements FallingBlock {
	private boolean drop = true;
	
    public CraftFallingSand(BukkitServer server, EntityFallingSand entity) {
        super(server, entity);
    }

    @Override
    public EntityFallingSand getHandle() {
        return (EntityFallingSand) entity;
    }

    @Override
    public String toString() {
        return "BukkitFallingSand";
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
