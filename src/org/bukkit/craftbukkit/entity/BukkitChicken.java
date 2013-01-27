package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityChicken;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitChicken extends BukkitAnimals implements Chicken {

    public BukkitChicken(BukkitServer server, EntityChicken entity) {
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
