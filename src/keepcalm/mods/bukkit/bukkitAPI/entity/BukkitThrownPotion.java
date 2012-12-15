package keepcalm.mods.bukkit.bukkitAPI.entity;

import java.util.Collection;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.projectile.EntityPotion;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitThrownPotion extends BukkitProjectile implements ThrownPotion {
    private Collection<PotionEffect> effects = null;

    public BukkitThrownPotion(BukkitServer server, EntityPotion entity) {
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
