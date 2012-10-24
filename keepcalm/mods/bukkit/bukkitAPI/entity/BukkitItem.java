package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.item.BukkitItemStack;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.craftbukkit.inventory.BukkitItemStack;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitItem extends BukkitEntity implements Item {
    private final EntityItem item;

    public BukkitItem(BukkitServer server, Entity entity, EntityItem item) {
        super(server, entity);
        this.item = item;
    }

    public BukkitItem(BukkitServer server, EntityItem entity) {
        this(server, entity, entity);
    }

    public ItemStack getItemStack() {
        return new BukkitItemStack(item.item);
    }

    public void setItemStack(ItemStack stack) {
        item.item = BukkitItemStack.createNMSItemStack(stack);
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
