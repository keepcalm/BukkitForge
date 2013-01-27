package org.bukkit.craftbukkit.entity;

//import org.bukkit.craftbukkit.entity.BukkitMinecart;
import net.minecraft.entity.item.EntityMinecart;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.PoweredMinecart;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftPoweredMinecart extends BukkitMinecart implements PoweredMinecart {
    public CraftPoweredMinecart(BukkitServer server, EntityMinecart entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "BukkitPoweredMinecart";
    }
}
