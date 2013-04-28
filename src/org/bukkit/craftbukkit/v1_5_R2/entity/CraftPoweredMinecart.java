package org.bukkit.craftbukkit.v1_5_R2.entity;

//import org.bukkit.craftbukkit.entity.CraftMinecart;
import net.minecraft.entity.item.EntityMinecart;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.PoweredMinecart;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftPoweredMinecart extends CraftMinecart implements PoweredMinecart {
    public CraftPoweredMinecart(CraftServer server, EntityMinecart entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftPoweredMinecart";
    }
}
