package org.bukkit.craftbukkit.v1_5_R2.inventory;

import java.util.Arrays;
import java.util.HashMap;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import keepcalm.mods.bukkit.ToBukkit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class CraftInventoryPlayer extends CraftInventory implements org.bukkit.inventory.PlayerInventory {
	private EntityPlayer player;
	private InventoryPlayer realInv;
	public CraftInventoryPlayer(InventoryPlayer inventory) {
		super(inventory);
		this.realInv = inventory;
		this.player = inventory.player;
	}

	@Override
	public ItemStack[] getArmorContents() {
		ItemStack[] armorContents = new ItemStack[realInv.armorInventory.length];
		int i = 0;
		for (net.minecraft.item.ItemStack j : realInv.armorInventory) {
			armorContents[i] = new CraftItemStack(j);
			i++;
		}
		return armorContents;
	}

    @Override
    public ItemStack[] getContents() {
        return Lists.transform(Arrays.asList(((InventoryPlayer) getInventory()).mainInventory), new Function<net.minecraft.item.ItemStack, Object>() {
            public Object apply(@Nullable net.minecraft.item.ItemStack itemStack) {
                return ToBukkit.itemStack(itemStack);
            }
        }).toArray( new ItemStack[0] );
    }

    @Override
    public void setContents(ItemStack[] items) {
        if (getContents().length < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getContents().length + " or less");
        }

        net.minecraft.item.ItemStack[] mcItems = getMCContents();

        for (int i = 0; i < mcItems.length; i++) {
            if (i >= items.length) {
                mcItems[i] = null;
            } else {
                mcItems[i] = CraftItemStack.createNMSItemStack(items[i]);
            }
        }
    }

	@Override
	public ItemStack getHelmet() {
		return new CraftItemStack(realInv.armorItemInSlot(3));
	}

	@Override
	public ItemStack getChestplate() {
		return new CraftItemStack(realInv.armorItemInSlot(2));
	}

	@Override
	public ItemStack getLeggings() {
		return new CraftItemStack(realInv.armorItemInSlot(1));
	}

	@Override
	public ItemStack getBoots() {
		return new CraftItemStack(realInv.armorItemInSlot(0));
	}

	@Override
	public void setArmorContents(ItemStack[] items) {
		
		for (int i = 0; i < 4; i++) {
			realInv.armorInventory[i] = ((CraftItemStack) items[i]).getHandle();
		}
		
	}

	private net.minecraft.item.ItemStack getItemStackOrNull(CraftItemStack stack) {
		return stack == null ? null : stack.getHandle();
	}
	
	@Override
	public void setHelmet(ItemStack helmet) {
		realInv.armorInventory[3] = ((CraftItemStack) helmet).getHandle();
		
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		realInv.armorInventory[2] = ((CraftItemStack) chestplate).getHandle();		
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		realInv.armorInventory[1] = ((CraftItemStack) leggings).getHandle();		
	}

	@Override
	public void setBoots(ItemStack boots) {
		realInv.armorInventory[0] = ((CraftItemStack) boots).getHandle();		
	}

	@Override
	public ItemStack getItemInHand() {
		return new CraftItemStack(realInv.getCurrentItem());
	}

	@Override
	public void setItemInHand(ItemStack stack) {
		realInv.setInventorySlotContents(getHeldItemSlot(), CraftItemStack.createNMSItemStack(stack));		
	}
	
	@Override
	public HumanEntity getHolder() {
		return (HumanEntity) CraftEntity.getEntity((CraftServer) Bukkit.getServer(), (Entity) this.player);
	}

	@Override
	public int getHeldItemSlot() {
		return realInv.currentItem;
	}

	@Override
	public int clear(int id, int data) {
		return realInv.clearInventory(id, data);
	}

	
	@Override
	public HashMap<Integer,ItemStack> addItem(ItemStack... items) {
		HashMap<Integer,ItemStack> leftover = new HashMap<Integer, ItemStack>();
		
		InventoryPlayer inv = (InventoryPlayer)getInventory();
		int count = 0;
		for (ItemStack i : items ) {
			net.minecraft.item.ItemStack internal = CraftItemStack.createNMSItemStack(i);
			if (!inv.addItemStackToInventory(internal)) {
				int origsize = internal.stackSize;
				internal.stackSize--;
				
				while (!inv.addItemStackToInventory(internal)) {
					internal.stackSize--;
					if (internal.stackSize == 0) {
						break;
					}
				}
				
				leftover.put(count, new CraftItemStack(internal.itemID, origsize - internal.stackSize, (short) internal.getItemDamage()));
				 
			}
		count++;	
		}
		return leftover;
		
	}

}
