package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.passive.EntityTameable;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Tameable;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftTameableAnimal extends CraftAnimals implements Tameable, Creature {
    public CraftTameableAnimal(CraftServer server, EntityTameable entity) {
        super(server, entity);
    }

    @Override
    public EntityTameable getHandle() {
        return (EntityTameable)super.getHandle();
    }

    public AnimalTamer getOwner() {
        if (("").equals(getOwnerName())) return null;

        AnimalTamer owner = getServer().getPlayerExact(getOwnerName());
        if (owner == null) {
            owner = getServer().getOfflinePlayer(getOwnerName());
        }

        return owner;
    }

    public String getOwnerName() {
        return getHandle().getOwnerName();
    }

    public boolean isTamed() {
        return getHandle().isTamed();
    }

    public void setOwner(AnimalTamer tamer) {
        if (tamer != null) {
            setTamed(true);
            getHandle().setPathToEntity(null);
            setOwnerName(tamer.getName());
        } else {
            setTamed(false);
            setOwnerName("");
        }
    }

    public void setOwnerName(String ownerName) {
        getHandle().setOwner(ownerName == null ? "" : ownerName);
    }

    public void setTamed(boolean tame) {
        getHandle().setTamed(tame);
        if (!tame) {
            setOwnerName("");
        }
    }

    public boolean isSitting() {
        return getHandle().isSitting();
    }

    public void setSitting(boolean sitting) {
        getHandle().setSitting(sitting);
        getHandle().setPathToEntity(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{owner=" + getOwner() + ",tamed=" + isTamed() + "}";
    }
}
