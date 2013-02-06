package keepcalm.mods.bukkit.forgeHandler;

import java.util.Iterator;

import keepcalm.mods.bukkit.BukkitContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftPlayerCache;
import org.bukkit.craftbukkit.inventory.BukkitRecipe;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
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
			
			InventoryView what = new CraftInventoryView(CraftPlayerCache.getCraftPlayer(fp), new CraftInventoryCrafting(inv, player.inventory), inv.eventHandler);

            // Should be slot of crafting container, will only be trouble if ever have crafting table with more than one slot
			CraftItemEvent ev = new CraftItemEvent(recipe, what, SlotType.CRAFTING, 0, false, false);
			Bukkit.getPluginManager().callEvent(ev);
			if (ev.isCancelled()) {
                // Believe it is too late to do this, this just clears the workbench which has already happened
				//if (!(inv.eventHandler instanceof ContainerWorkbench)) return;
				//ContainerWorkbench cc = (ContainerWorkbench) inv.eventHandler;
				//InventoryCraftResult iv = (InventoryCraftResult) cc.craftResult;
				//iv.setInventorySlotContents(0, null);

                int itemsToRemove = item.stackSize > 0 ? item.stackSize : 1;

                // Is item in hand (single craft)
                if( ( player.inventory.getItemStack() != null ) && ( player.inventory.getItemStack().getItem().itemID == item.getItem().itemID )  )
                {
                    itemsToRemove = itemsToRemove - player.inventory.getItemStack().stackSize;
                    player.inventory.setItemStack(null); // Remove it
                    ((EntityPlayerMP)player).updateHeldItem();
                }

                // If item not removed from hand, or not all crafted were removed from hand
                if(itemsToRemove > 0)
                {
                    for(int i = 0; i < player.inventory.getSizeInventory(); i++)
                    {
                        if( player.inventory.getStackInSlot(i) != null && player.inventory.getStackInSlot(i).getItem().itemID == item.getItem().itemID )
                        {
                            int stackSize = ((InventoryPlayer)player.inventory).getStackInSlot(i).stackSize;
                            if( stackSize < itemsToRemove )
                            {
                                ((InventoryPlayer)player.inventory).decrStackSize(i, stackSize);
                                itemsToRemove = itemsToRemove - stackSize;
                            }
                            else
                            {
                                ((InventoryPlayer)player.inventory).decrStackSize(i, itemsToRemove);
                                itemsToRemove = 0;
                            }

                            if( itemsToRemove == 0 ) break;
                        }
                    }
                }

                // Add ingredients back
                for(int i = 0; i < inv.getSizeInventory(); i++)
                {
                    if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).stackSize != 0 )
                    {
                        ItemStack putBack = inv.getStackInSlot(i).copy();
                        putBack.stackSize = 1;
                        player.inventory.addItemStackToInventory( putBack );
                    }
                }
			}
		}
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
		
	}

}
