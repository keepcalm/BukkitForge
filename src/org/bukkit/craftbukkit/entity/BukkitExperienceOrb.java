package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityXPOrb;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitExperienceOrb extends BukkitEntity implements ExperienceOrb {
    public BukkitExperienceOrb(BukkitServer server, EntityXPOrb entity) {
        super(server, entity);
    }

    public int getExperience() {
        return getHandle().getXpValue();
    }

    public void setExperience(int value) {
        getHandle().xpValue = value;
    }

    @Override
    public EntityXPOrb getHandle() {
        return (EntityXPOrb) entity;
    }

    @Override
    public String toString() {
        return "BukkitExperienceOrb";
    }

    public EntityType getType() {
        return EntityType.EXPERIENCE_ORB;
    }
}
