package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityEnderCrystal;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftEnderCrystal extends BukkitEntity implements EnderCrystal {
    public CraftEnderCrystal(BukkitServer server, EntityEnderCrystal entity) {
        super(server, entity);
    }

    @Override
    public EntityEnderCrystal getHandle() {
        return (EntityEnderCrystal) entity;
    }

    @Override
    public String toString() {
        return "BukkitEnderCrystal";
    }

    public EntityType getType() {
        return EntityType.ENDER_CRYSTAL;
    }
}
