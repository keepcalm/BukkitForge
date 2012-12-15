package keepcalm.mods.bukkit.bukkitAPI.entity;

//import org.bukkit.craftbukkit.entity.BukkitMinecart;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.item.EntityMinecart;

import org.bukkit.entity.PoweredMinecart;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitPoweredMinecart extends BukkitMinecart implements PoweredMinecart {
    public BukkitPoweredMinecart(BukkitServer server, EntityMinecart entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "BukkitPoweredMinecart";
    }
}
