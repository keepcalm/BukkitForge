package keepcalm.mods.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EventFactory {
	public static boolean onBlockHarvested(World world, int x, int y, int z, Block block, int metadata, EntityPlayer entityPlayer) {
		PlayerBreakBlockEvent ev = new PlayerBreakBlockEvent(world,x,y,z,block,metadata,entityPlayer);
		MinecraftForge.EVENT_BUS.post(ev);
		
		
		return ev.isCanceled();
	}
}
