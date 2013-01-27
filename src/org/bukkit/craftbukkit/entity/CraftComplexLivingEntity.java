package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.boss.EntityDragon;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.ComplexLivingEntity;
//import org.bukkit.craftbukkit.CraftServer;

public abstract class CraftComplexLivingEntity extends BukkitLivingEntity implements ComplexLivingEntity {
    public CraftComplexLivingEntity(BukkitServer server, EntityDragon entity) {
        super(server, entity);
    }

    @Override
    public EntityDragon getHandle() {
        return (EntityDragon) entity;
    }

    @Override
    public String toString() {
        return "BukkitComplexLivingEntity";
    }
}
