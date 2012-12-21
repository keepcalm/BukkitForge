package keepcalm.mods.bukkit;

import keepcalm.mods.bukkit.events.DispenseItemEvent;
import keepcalm.mods.bukkit.events.PlayerDamageBlockEvent;
import keepcalm.mods.bukkit.events.PlayerUseItemEvent;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;

public class ForgeEventHelper {
	public static void onItemUse(ItemStack stack, EntityPlayer who, World world, int x, int y, int z, int blockFace) {
		if (Side.CLIENT.isClient())
			// not on client
			return;
		System.out.println("ItemUse!");
		//System.out.println("Hello");
		PlayerUseItemEvent ev = new PlayerUseItemEvent(stack, who, world, x, y, z, ForgeDirection.getOrientation(blockFace));
		MinecraftForge.EVENT_BUS.post(ev);
	}
	
	public static void onBlockDamage(ItemInWorldManager man) {
		
		// mcp has ridiculously long names
		
		PlayerDamageBlockEvent ev = new PlayerDamageBlockEvent(man.thisPlayerMP, man.partiallyDestroyedBlockX,
				man.partiallyDestroyedBlockY, man.partiallyDestroyedBlockZ,
				man.theWorld, man.curblockDamage, man.durabilityRemainingOnBlock);
		
		MinecraftForge.EVENT_BUS.post(ev);
		
	}
	
	/**
	 * 
	 * @param block
	 * @param itemToDispense
	 * @param x
	 * @param y
	 * @param z
	 * @return whether to cancel or not (true == cancelled)
	 */
	public static boolean onDispenseItem(World world, int x, int y, int z, ItemStack itemToDispense) {
		DispenseItemEvent ev = new DispenseItemEvent(x, y, z, world, itemToDispense);
		System.out.println("Dispense!");
		MinecraftForge.EVENT_BUS.post(ev);
		
		if (ev.isCanceled()) {
			return true;
		}
		
		return false;
	}
	
}
