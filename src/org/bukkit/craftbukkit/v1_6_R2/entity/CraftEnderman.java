package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.monster.EntityEnderman;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

public class CraftEnderman extends CraftMonster implements Enderman {
    public CraftEnderman(CraftServer server, EntityEnderman entity) {
        super(server, entity);
    }

    public MaterialData getCarriedMaterial() {
        return Material.getMaterial(getHandle().getCarried()).getNewData((byte) getHandle().getCarryingData());
    }

    public void setCarriedMaterial(MaterialData data) {
        getHandle().setCarried(data.getItemTypeId());
        getHandle().setCarryingData(data.getData());
    }

    @Override
    public EntityEnderman getHandle() {
        return (EntityEnderman) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderman";
    }

    public EntityType getType() {
        return EntityType.ENDERMAN;
    }
}
