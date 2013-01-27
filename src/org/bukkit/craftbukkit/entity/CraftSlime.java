package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntitySlime;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftSlime extends BukkitLivingEntity implements Slime {

    public CraftSlime(BukkitServer server, EntitySlime entity) {
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
