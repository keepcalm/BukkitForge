package org.bukkit.craftbukkit.v1_5_R2.inventory;

import net.minecraft.item.Item;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.ItemStack;

// it might be important...
@DelegateDeserialization(ItemStack.class)
public class CraftItemStack extends ItemStack {
	protected net.minecraft.item.ItemStack item;

    public CraftItemStack(net.minecraft.item.ItemStack item) {
        super(
            item != null ? item.itemID: 0,
            item != null ? item.stackSize : 0,
            (short)(item != null ? item.getItemDamage() : 0)
        );
        this.item = item;
    }

    public CraftItemStack(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability());
        addUnsafeEnchantments(item.getEnchantments());
    }

    /* 'Overwritten' constructors from ItemStack, yay for Java sucking */
    public CraftItemStack(final int type) {
        this(type, 1);
    }

    public CraftItemStack(final Material type) {
        this(type, 1);
    }

    public CraftItemStack(final int type, final int amount) {
        this(type, amount, (byte) 0);
    }

    public CraftItemStack(final Material type, final int amount) {
        this(type.getId(), amount);
    }

    public CraftItemStack(final int type, final int amount, final short damage) {
        this(type, amount, damage, null);
    }

    public CraftItemStack(final Material type, final int amount, final short damage) {
        this(type.getId(), amount, damage);
    }

    public CraftItemStack(final Material type, final int amount, final short damage, final Byte data) {
        this(type.getId(), amount, damage, data);
    }

    public CraftItemStack(int type, int amount, short damage, Byte data) {
        this(new net.minecraft.item.ItemStack(type, amount, data != null ? data : damage));
    }
    public static net.minecraft.item.ItemStack createNMSItemStack(ItemStack original) {
        if (original == null || original.getTypeId() <= 0) {
            return null;
        } else if (original instanceof CraftItemStack) {
            return ((CraftItemStack) original).getHandle();
        }
        return new CraftItemStack(original).getHandle();
    }
    public net.minecraft.item.ItemStack getHandle() {
    	return this.item;
    }

	public static CraftItemStack asCraftMirror(
			net.minecraft.item.ItemStack par2ItemStack) {
		return new CraftItemStack(par2ItemStack);
	}

	public static net.minecraft.item.ItemStack asNMSCopy(
			org.bukkit.inventory.ItemStack item2) {
		// TODO Auto-generated method stub
		return createNMSItemStack(item2);
	}

	public static ItemStack asNewCraftStack(Item item, int j) {
		return new CraftItemStack(new net.minecraft.item.ItemStack(item, j));
	}
	
	public void setAmount(int amount) {
		super.setAmount(amount);
		this.item.stackSize = amount > getMaxStackSize() ? getMaxStackSize() : amount;
	}
	
	public void setDurability(final short durability) {
        super.setDurability(durability);
		this.item.setItemDamage(durability);
    }
}
