package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

//import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Projectile;

public abstract class AbstractProjectile extends BukkitEntity implements Projectile {

    private boolean doesBounce;

    public AbstractProjectile(BukkitServer server, net.minecraft.src.Entity entity) {
        super(server, entity);
        doesBounce = false;
    }

    public boolean doesBounce() {
        return doesBounce;
    }

    public void setBounce(boolean doesBounce) {
        this.doesBounce = doesBounce;
    }

}
