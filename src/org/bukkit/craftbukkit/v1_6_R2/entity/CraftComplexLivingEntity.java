package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.boss.EntityDragon;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.ComplexLivingEntity;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

public abstract class CraftComplexLivingEntity extends CraftLivingEntity implements ComplexLivingEntity {
    public CraftComplexLivingEntity(CraftServer server, EntityDragon entity) {
        super(server, entity);
    }

    @Override
    public EntityDragon getHandle() {
        return (EntityDragon) entity;
    }

    @Override
    public String toString() {
        return "CraftComplexLivingEntity";
    }
}
