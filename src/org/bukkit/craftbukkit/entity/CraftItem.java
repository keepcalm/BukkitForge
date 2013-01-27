package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.craftbukkit.inventory.BukkitItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.craftbukkit.inventory.BukkitItemStack;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftItem extends BukkitEntity implements Item {
    private final EntityItem item;

    public CraftItem(BukkitServer server, Entity entity, EntityItem item) {
        super(server, entity);
        this.item = item;
    }

    public CraftItem(BukkitServer server, EntityItem entity) {
        this(server, entity, entity);
    }

    public ItemStack getItemStack() {
        return new CraftItemStack(item.getEntityItem());
    }

    public void setItemStack(ItemStack stack) {
        item.func_92058_a(BukkitItemStack.createNMSItemStack(stack));
    }

    public int getPickupDelay() {
        return item.delayBeforeCanPickup;
    }

    public void setPickupDelay(int delay) {
        item.delayBeforeCanPickup = delay;
    }

    @Override
    public String toString() {
        return "BukkitItem";
    }

    public EntityType getType() {
        return EntityType.DROPPED_ITEM;
    }
}
