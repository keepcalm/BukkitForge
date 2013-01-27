package org.bukkit.craftbukkit.inventory;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.craftbukkit.entity.BukkitEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryPlayer extends BukkitInventory implements org.bukkit.inventory.PlayerInventory {
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
			realInv.armorInventory[i] = ((BukkitItemStack) items[i]).getHandle();
		}
		
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		realInv.armorInventory[3] = ((BukkitItemStack) helmet).getHandle();
		
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		realInv.armorInventory[2] = ((BukkitItemStack) chestplate).getHandle();		
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		realInv.armorInventory[1] = ((BukkitItemStack) leggings).getHandle();		
	}

	@Override
	public void setBoots(ItemStack boots) {
		realInv.armorInventory[0] = ((BukkitItemStack) boots).getHandle();		
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
		return (HumanEntity) CraftEntity.getEntity((BukkitServer) Bukkit.getServer(), (Entity) this.player);
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
		
		InventoryPlayer inv = (InventoryPlayer) inventory;
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
