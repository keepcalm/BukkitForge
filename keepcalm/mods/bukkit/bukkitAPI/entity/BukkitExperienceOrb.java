package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

import net.minecraft.src.EntityXPOrb;

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
