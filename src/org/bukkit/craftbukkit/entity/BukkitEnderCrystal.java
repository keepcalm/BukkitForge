package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityEnderCrystal;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitEnderCrystal extends BukkitEntity implements EnderCrystal {
    public BukkitEnderCrystal(BukkitServer server, EntityEnderCrystal entity) {
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
