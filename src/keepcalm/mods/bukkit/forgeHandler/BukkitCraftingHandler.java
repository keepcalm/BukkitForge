package keepcalm.mods.bukkit.forgeHandler;

import java.util.Iterator;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryCrafting;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryView;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitRecipe;
import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;

import cpw.mods.fml.common.ICraftingHandler;


// TODO
public class BukkitCraftingHandler implements ICraftingHandler {

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item,
			IInventory craftMatrix) {


		if (craftMatrix instanceof InventoryCrafting) {

			EntityPlayerMP fp;
			if (player instanceof EntityPlayerMP) fp = (EntityPlayerMP) player;
			else fp = BukkitContainer.MOD_PLAYER;
			
			InventoryCrafting inv = (InventoryCrafting) craftMatrix;
			Iterator<IRecipe> irec = (Iterator<IRecipe>) CraftingManager.getInstance().recipes.iterator();
			IRecipe targ = null;
			while (irec.hasNext()) {
				IRecipe r = irec.next();
				if (r.matches(inv, player.worldObj)) {
					targ = r;
					break;
				}
			}
			
			if (targ == null) return;
			
			
			
			Recipe recipe = new BukkitRecipe(targ);
			
			
			InventoryView what = new BukkitInventoryView(BukkitPlayerCache.getBukkitPlayer(fp), new BukkitInventoryCrafting(inv, player.inventory), inv.eventHandler);
			CraftItemEvent ev = new CraftItemEvent(recipe, what, SlotType.CRAFTING, -1, false, false);
			Bukkit.getPluginManager().callEvent(ev);
			if (ev.isCancelled()) {
				// failure!
				if (!(inv.eventHandler instanceof ContainerWorkbench)) return;
				ContainerWorkbench cc = (ContainerWorkbench) inv.eventHandler;
				InventoryCraftResult iv = (InventoryCraftResult) cc.craftResult;
				iv.setInventorySlotContents(0, null);
			}
		}

	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
		
	}

}
