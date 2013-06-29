package org.bukkit.craftbukkit.v1_5_R3.entity;

import java.util.Random;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class CraftFirework extends CraftEntity implements Firework {
    private static final int FIREWORK_ITEM_INDEX = 8;

    private final Random random = new Random();
    private final CraftItemStack item;

    public CraftFirework(CraftServer server, net.minecraft.entity.item.EntityFireworkRocket entity) {
        super(server, entity);

        net.minecraft.item.ItemStack item = getHandle().getDataWatcher().getWatchableObjectItemStack(FIREWORK_ITEM_INDEX);

        if (item == null) {
            item = new net.minecraft.item.ItemStack(net.minecraft.item.Item.firework);
            getHandle().getDataWatcher().updateObject(FIREWORK_ITEM_INDEX, item);
        }

        this.item = CraftItemStack.asCraftMirror(item);

        // Ensure the item is a firework...
        if (this.item.getType() != Material.FIREWORK) {
            this.item.setType(Material.FIREWORK);
        }
    }

    @Override
    public net.minecraft.entity.item.EntityFireworkRocket getHandle() {
        return (net.minecraft.entity.item.EntityFireworkRocket) entity;
    }

    @Override
    public String toString() {
        return "CraftFirework";
    }

    public EntityType getType() {
        return EntityType.FIREWORK;
    }

    public FireworkMeta getFireworkMeta() {
        return (FireworkMeta) item.getItemMeta();
    }

    public void setFireworkMeta(FireworkMeta meta) {
        item.setItemMeta(meta);

        // Copied from EntityFireworks constructor, update firework lifetime/power
        getHandle().lifetime = 10 * (1 + meta.getPower()) + random.nextInt(6) + random.nextInt(7);

        getHandle().getDataWatcher().setObjectWatched(FIREWORK_ITEM_INDEX); // Update
    }
}
