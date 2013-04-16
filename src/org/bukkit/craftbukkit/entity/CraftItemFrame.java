package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;

public class CraftItemFrame extends CraftHanging implements ItemFrame {
    public CraftItemFrame(CraftServer server, EntityItemFrame entity) {
        super(server, entity);
    }

    public void setItem(org.bukkit.inventory.ItemStack item) {
        if (item == null || item.getTypeId() == 0) {
        	// guesses, TODO check this is right
            getHandle().getDataWatcher().addObjectByDataType(2, 5);
            getHandle().getDataWatcher().setObjectWatched(2);
        } else {
            getHandle().setDisplayedItem(CraftItemStack.createNMSItemStack(item));
        }
    }

    public org.bukkit.inventory.ItemStack getItem() {
        ItemStack i = getHandle().getDisplayedItem();
        return i == null ? new org.bukkit.inventory.ItemStack(Material.AIR) : new CraftItemStack(i);
    }

    public Rotation getRotation() {
        return toCraftRotation(getHandle().getRotation());
    }

    Rotation toCraftRotation(int value) {
        // Translate NMS rotation integer to Craft API
        switch (value) {
        case 0:
            return Rotation.NONE;
        case 1:
            return Rotation.CLOCKWISE;
        case 2:
            return Rotation.FLIPPED;
        case 3:
            return Rotation.COUNTER_CLOCKWISE;
        default:
            throw new AssertionError("Unknown rotation " + getHandle().getRotation() + " for " + getHandle());
        }
    }

    public void setRotation(Rotation rotation) {
        Validate.notNull(rotation, "Rotation cannot be null");
        getHandle().setItemRotation(toInteger(rotation));
    }

    static int toInteger(Rotation rotation) {
        // Translate Craft API rotation to NMS integer
        switch (rotation) {
        case NONE:
            return 0;
        case CLOCKWISE:
            return 1;
        case FLIPPED:
            return 2;
        case COUNTER_CLOCKWISE:
            return 3;
        default:
            throw new IllegalArgumentException(rotation + " is not applicable to an ItemFrame");
        }
    }

    @Override
    public EntityItemFrame getHandle() {
        return (EntityItemFrame) entity;
    }

    @Override
    public String toString() {
        return "CraftItemFrame{item=" + getItem() + ", rotation=" + getRotation() + "}";
    }

    public EntityType getType() {
        return EntityType.ITEM_FRAME;
    }
}
