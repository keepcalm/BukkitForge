package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.effect.EntityLightningBolt;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitLightningStrike extends BukkitEntity implements LightningStrike {
    public BukkitLightningStrike(final BukkitServer server, final EntityLightningBolt entity) {
        super(server, entity);
    }

    public boolean isEffect() {
        return false;
    }

    @Override
    public EntityLightningBolt getHandle() {
        return (EntityLightningBolt) entity;
    }

    @Override
    public String toString() {
        return "BukkitLightningStrike";
    }

    public EntityType getType() {
        return EntityType.LIGHTNING;
    }
}
