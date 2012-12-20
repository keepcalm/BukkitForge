package keepcalm.mods.bukkit;

import keepcalm.mods.bukkit.events.PlayerUseItemEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class ForgeEventHelper {
	public static void onItemUse(ItemStack stack, EntityPlayer who, World world, int x, int y, int z, int blockFace) {
		
		System.out.println("Hello");
		PlayerUseItemEvent ev = new PlayerUseItemEvent(stack, who, world, x, y, z, ForgeDirection.getOrientation(blockFace));
		System.out.println("POSTing event: " + ev);
		MinecraftForge.EVENT_BUS.post(ev);
	}
	
}
