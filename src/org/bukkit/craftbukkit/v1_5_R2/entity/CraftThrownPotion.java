package org.bukkit.craftbukkit.v1_5_R2.entity;

import java.util.Collection;

import net.minecraft.entity.projectile.EntityPotion;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftThrownPotion extends CraftProjectile implements ThrownPotion {
    private Collection<PotionEffect> effects = null;

    public CraftThrownPotion(CraftServer server, EntityPotion entity) {
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
        return "CraftThrownPotion";
    }

    public EntityType getType() {
        return EntityType.SPLASH_POTION;
    }

	@Override
	public ItemStack getItem() {
        // We run this method once since it will set the item stack if there is none.
        getHandle().getPotionDamage();

        return CraftItemStack.asBukkitCopy(getHandle().potionDamage);
    }

    public void setItem(ItemStack item) {
        // The ItemStack must not be null.
        Validate.notNull(item, "ItemStack cannot be null.");

        // The ItemStack must be a potion.
        Validate.isTrue(item.getType() == Material.POTION, "ItemStack must be a potion. This item stack was " + item.getType() + ".");

        getHandle().potionDamage = CraftItemStack.asNMSCopy(item);
    }
}
