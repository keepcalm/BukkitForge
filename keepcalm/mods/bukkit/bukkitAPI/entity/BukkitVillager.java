package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityVillager;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitVillager extends BukkitAgeable implements Villager {
    public BukkitVillager(BukkitServer server, EntityVillager entity) {
        super(server, entity);
    }

    @Override
    public EntityVillager getHandle() {
        return (EntityVillager) entity;
    }

    @Override
    public String toString() {
        return "BukkitVillager";
    }

    public EntityType getType() {
        return EntityType.VILLAGER;
    }

    public Profession getProfession() {
        return Profession.getProfession(getHandle().getProfession());
    }

    public void setProfession(Profession profession) {
        Validate.notNull(profession);
        getHandle().setProfession(profession.getId());
    }
}
