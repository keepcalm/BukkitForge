package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.passive.EntitySheep;

import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftSheep extends CraftAnimals implements Sheep {
    public CraftSheep(CraftServer server, EntitySheep entity) {
        super(server, entity);
    }

    public DyeColor getColor() {
        return DyeColor.getByData((byte) getHandle().getFleeceColor());
    }

    public void setColor(DyeColor color) {
        getHandle().setFleeceColor(color.getData());
    }

    public boolean isSheared() {
        return getHandle().getSheared();
    }

    public void setSheared(boolean flag) {
        getHandle().setSheared(flag);
    }

    @Override
    public EntitySheep getHandle() {
        return (EntitySheep) entity;
    }

    @Override
    public String toString() {
        return "CraftSheep";
    }

    public EntityType getType() {
        return EntityType.SHEEP;
    }
}
