package keepcalm.mods.bukkit.bukkitAPI.inventory;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntity;
import keepcalm.mods.bukkit.bukkitAPI.item.BukkitItemStack;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.InventoryPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryPlayer extends BukkitInventory implements org.bukkit.inventory.PlayerInventory {
	private EntityPlayerMP player;
	private InventoryPlayer realInv;
	public BukkitInventoryPlayer(InventoryPlayer inventory) {
		super(inventory);
		this.realInv = inventory;
		this.player = (EntityPlayerMP) inventory.player;
	}

	@Override
	public ItemStack[] getArmorContents() {
		ItemStack[] armorContents = new ItemStack[realInv.armorInventory.length];
		int i = 0;
		for (net.minecraft.src.ItemStack j : realInv.armorInventory) {
			armorContents[i] = new BukkitItemStack(j);
		}
		return armorContents;
	}

	@Override
	public ItemStack getHelmet() {
		return new BukkitItemStack(realInv.armorItemInSlot(0));
	}

	@Override
	public ItemStack getChestplate() {
		return new BukkitItemStack(realInv.armorItemInSlot(1));
	}

	@Override
	public ItemStack getLeggings() {
		return new BukkitItemStack(realInv.armorItemInSlot(2));
	}

	@Override
	public ItemStack getBoots() {
		return new BukkitItemStack(realInv.armorItemInSlot(3));
	}

	@Override
	public void setArmorContents(ItemStack[] items) {
		
		for (int i = 0; i < 4; i++) {
			realInv.armorInventory[i] = ((BukkitItemStack) items[i]).getHandle();
		}
		
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		realInv.armorInventory[0] = ((BukkitItemStack) helmet).getHandle();
		
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		realInv.armorInventory[1] = ((BukkitItemStack) chestplate).getHandle();		
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		realInv.armorInventory[2] = ((BukkitItemStack) leggings).getHandle();		
	}

	@Override
	public void setBoots(ItemStack boots) {
		realInv.armorInventory[3] = ((BukkitItemStack) boots).getHandle();		
	}

	@Override
	public ItemStack getItemInHand() {
		return new BukkitItemStack(realInv.getCurrentItem());
	}

	@Override
	public void setItemInHand(ItemStack stack) {
		realInv.setInventorySlotContents(getHeldItemSlot(), ((BukkitItemStack) stack).getHandle());		
	}
	
	@Override
	public HumanEntity getHolder() {
		return (HumanEntity) BukkitEntity.getEntity((BukkitServer) Bukkit.getServer(), (Entity) this.player);
	}

	@Override
	public int getHeldItemSlot() {
		return realInv.currentItem;
	}

	@Override
	public int clear(int id, int data) {
		return realInv.clearInventory(id, data);
	}

}
