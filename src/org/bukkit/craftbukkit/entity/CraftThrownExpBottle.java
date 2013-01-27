package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityExpBottle;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownExpBottle;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftThrownExpBottle extends BukkitProjectile implements ThrownExpBottle {
    public CraftThrownExpBottle(BukkitServer server, EntityExpBottle entity) {
        super(server, entity);
    }

    @Override
    public EntityExpBottle getHandle() {
        return (EntityExpBottle) entity;
    }

    @Override
    public String toString() {
        return "BukkitThrownExpBottle";
    }

    public EntityType getType() {
        return EntityType.THROWN_EXP_BOTTLE;
    }
}
