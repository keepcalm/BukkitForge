package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.item.EntityEnderCrystal;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftEnderCrystal extends CraftEntity implements EnderCrystal {
    public CraftEnderCrystal(CraftServer server, EntityEnderCrystal entity) {
        super(server, entity);
    }

    @Override
    public EntityEnderCrystal getHandle() {
        return (EntityEnderCrystal) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderCrystal";
    }

    public EntityType getType() {
        return EntityType.ENDER_CRYSTAL;
    }
}
