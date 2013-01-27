package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.effect.EntityLightningBolt;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftLightningStrike extends CraftEntity implements LightningStrike {
    public CraftLightningStrike(final CraftServer server, final EntityLightningBolt entity) {
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
        return "CraftLightningStrike";
    }

    public EntityType getType() {
        return EntityType.LIGHTNING;
    }
}
