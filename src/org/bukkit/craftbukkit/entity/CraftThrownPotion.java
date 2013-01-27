package org.bukkit.craftbukkit.entity;

import java.util.Collection;

import net.minecraft.entity.projectile.EntityPotion;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftThrownPotion extends BukkitProjectile implements ThrownPotion {
    private Collection<PotionEffect> effects = null;

    public CraftThrownPotion(BukkitServer server, EntityPotion entity) {
        super(server, entity);
    }

    public Collection<PotionEffect> getEffects() {
        if (effects == null) {
            effects = Potion.getBrewer().getEffectsFromDamage(getHandle().getPotionDamage());
        }

        return effects;
    }

    @Override
    public EntityPotion getHandle() {
        return (EntityPotion) entity;
    }

    @Override
    public String toString() {
        return "BukkitThrownPotion";
    }

    public EntityType getType() {
        return EntityType.SPLASH_POTION;
    }
}
