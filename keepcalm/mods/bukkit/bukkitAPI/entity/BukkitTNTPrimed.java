package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityTNTPrimed;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitTNTPrimed extends BukkitEntity implements TNTPrimed {

    public BukkitTNTPrimed(BukkitServer server, EntityTNTPrimed entity) {
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
