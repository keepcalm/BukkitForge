package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.monster.EntitySlime;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitSlime extends BukkitLivingEntity implements Slime {

    public BukkitSlime(BukkitServer server, EntitySlime entity) {
        super(server, entity);
    }

    public int getSize() {
        return getHandle().getSlimeSize();
    }

    public void setSize(int size) {
        getHandle().setSlimeSize(size);
    }

    @Override
    public EntitySlime getHandle() {
        return (EntitySlime) entity;
    }

    @Override
    public String toString() {
        return "BukkitSlime";
    }

    public EntityType getType() {
        return EntityType.SLIME;
    }
}
