package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.boss.EntityDragon;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.ComplexLivingEntity;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

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
