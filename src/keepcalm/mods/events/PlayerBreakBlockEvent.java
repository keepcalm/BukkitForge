package keepcalm.mods.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class PlayerBreakBlockEvent extends Event {
		public final World world;
		public final int blockX;
		public final int blockY;
		public final int blockZ;
		public final Block block;
		public final int blockMeta;
		public final EntityPlayer player;
	    public PlayerBreakBlockEvent(World world, int x, int y, int z, Block block, int metadata, EntityPlayer entityPlayer) {
	    	super();
	    	this.world = world;
	    	blockX = x;
	    	blockY = y;
	    	blockZ = z;
	    	this.block = block;
	    	blockMeta = metadata;
	    	player = entityPlayer;
	    }
}
