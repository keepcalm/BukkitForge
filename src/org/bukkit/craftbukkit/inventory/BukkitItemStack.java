package org.bukkit.craftbukkit.inventory;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.ItemStack;

// it might be important...
@DelegateDeserialization(ItemStack.class)
public class BukkitItemStack extends ItemStack {
	protected net.minecraft.item.ItemStack item;

    public BukkitItemStack(net.minecraft.item.ItemStack item) {
        super(
            item != null ? item.itemID: 0,
            item != null ? item.stackSize : 0,
            (short)(item != null ? item.getItemDamage() : 0)
        );
        this.item = item;
    }

    public BukkitItemStack(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability());
        addUnsafeEnchantments(item.getEnchantments());
    }

    /* 'Overwritten' constructors from ItemStack, yay for Java sucking */
    public BukkitItemStack(final int type) {
        this(type, 1);
    }

    public BukkitItemStack(final Material type) {
        this(type, 1);
    }

    public BukkitItemStack(final int type, final int amount) {
        this(type, amount, (byte) 0);
    }

    public BukkitItemStack(final Material type, final int amount) {
        this(type.getId(), amount);
    }

    public BukkitItemStack(final int type, final int amount, final short damage) {
        this(type, amount, damage, null);
    }

    public BukkitItemStack(final Material type, final int amount, final short damage) {
        this(type.getId(), amount, damage);
    }

    public BukkitItemStack(final Material type, final int amount, final short damage, final Byte data) {
        this(type.getId(), amount, damage, data);
    }

    public BukkitItemStack(int type, int amount, short damage, Byte data) {
        this(new net.minecraft.item.ItemStack(type, amount, data != null ? data : damage));
    }
    public static net.minecraft.item.ItemStack createNMSItemStack(ItemStack original) {
        if (original == null || original.getTypeId() <= 0) {
            return null;
        } else if (original instanceof BukkitItemStack) {
            return ((BukkitItemStack) original).getHandle();
        }
        return new BukkitItemStack(original).getHandle();
    }
    public net.minecraft.item.ItemStack getHandle() {
    	return this.item;
    }
}
