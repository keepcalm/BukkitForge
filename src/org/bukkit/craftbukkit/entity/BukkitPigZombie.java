package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.EntityPigZombie;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitPigZombie extends BukkitZombie implements PigZombie {

    public BukkitPigZombie(BukkitServer server, EntityPigZombie entity) {
        super(server, entity);
    }

    public int getAnger() {
        return getHandle().angerLevel;
    }

    public void setAnger(int level) {
        getHandle().angerLevel = level;
    }

    public void setAngry(boolean angry) {
        setAnger(angry ? 400 : 0);
    }

    public boolean isAngry() {
        return getAnger() > 0;
    }

    @Override
    public EntityPigZombie getHandle() {
        return (EntityPigZombie) entity;
    }

    @Override
    public String toString() {
        return "BukkitPigZombie";
    }

    public EntityType getType() {
        return EntityType.PIG_ZOMBIE;
    }
}
