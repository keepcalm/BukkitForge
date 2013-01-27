package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.effect.EntityLightningBolt;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftLightningStrike extends BukkitEntity implements LightningStrike {
    public CraftLightningStrike(final BukkitServer server, final EntityLightningBolt entity) {
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
