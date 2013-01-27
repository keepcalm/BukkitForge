package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityTNTPrimed;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftTNTPrimed extends BukkitEntity implements TNTPrimed {

    public CraftTNTPrimed(BukkitServer server, EntityTNTPrimed entity) {
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
        return "BukkitTNTPrimed";
    }

    public EntityType getType() {
        return EntityType.PRIMED_TNT;
    }

}
