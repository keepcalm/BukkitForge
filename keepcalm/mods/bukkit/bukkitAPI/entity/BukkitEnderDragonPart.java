package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityDragonPart;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitEnderDragonPart extends BukkitComplexPart implements EnderDragonPart {
    public BukkitEnderDragonPart(BukkitServer server, EntityDragonPart entity) {
        super(server, entity);
    }

    @Override
    public EnderDragon getParent() {
        return (EnderDragon) super.getParent();
    }

    @Override
    public EntityDragonPart getHandle() {
        return (EntityDragonPart) entity;
    }

    @Override
    public String toString() {
        return "BukkitEnderDragonPart";
    }
}
