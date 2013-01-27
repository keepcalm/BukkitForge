package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityAnimal;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.Animals;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftAnimals extends BukkitAgeable implements Animals {

    public CraftAnimals(BukkitServer server, EntityAnimal entity) {
        super(server, entity);
    }

    @Override
    public EntityAnimal getHandle() {
        return (EntityAnimal) entity;
    }

    @Override
    public String toString() {
        return "BukkitAnimals";
    }
}
