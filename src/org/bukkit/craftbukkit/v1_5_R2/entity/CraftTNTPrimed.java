package org.bukkit.craftbukkit.v1_5_R2.entity;

import net.minecraft.entity.item.EntityTNTPrimed;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftTNTPrimed extends CraftEntity implements TNTPrimed {

    public CraftTNTPrimed(CraftServer server, EntityTNTPrimed entity) {
        super(server, entity);
    }
    
    public float getYield() {
        return 4.0F;
    }

    public boolean isIncendiary() {
        return true;
    }
    /**
     * Unimplemented
     */
    public void setIsIncendiary(boolean isIncendiary) {
        // NOOP.
    }
    /**
     * Unimplemented
     */
    public void setYield(float yield) {
        // NOOP.
    }

    public int getFuseTicks() {
        return getHandle().fuse;
    }

    public void setFuseTicks(int fuseTicks) {
        getHandle().fuse = fuseTicks;
    }

    @Override
    public EntityTNTPrimed getHandle() {
        return (EntityTNTPrimed) entity;
    }

    @Override
    public String toString() {
        return "CraftTNTPrimed";
    }

    public EntityType getType() {
        return EntityType.PRIMED_TNT;
    }

	@Override
	public Entity getSource() {
        return null;
    }

}
