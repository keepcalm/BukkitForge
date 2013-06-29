package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.passive.EntityChicken;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftChicken extends CraftAnimals implements Chicken {

    public CraftChicken(CraftServer server, EntityChicken entity) {
        super(server, entity);
    }

    @Override
    public EntityChicken getHandle() {
        return (EntityChicken) entity;
    }

    @Override
    public String toString() {
        return "CraftChicken";
    }

    public EntityType getType() {
        return EntityType.CHICKEN;
    }
}
