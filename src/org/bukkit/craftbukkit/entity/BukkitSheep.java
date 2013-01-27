package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntitySheep;

import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitSheep extends BukkitAnimals implements Sheep {
    public BukkitSheep(BukkitServer server, EntitySheep entity) {
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
        return "BukkitSheep";
    }

    public EntityType getType() {
        return EntityType.SHEEP;
    }
}
