package keepcalm.mods.bukkit.events;

import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * 
 * 
 * called when a block is destroyed by something that isn't a player
 * use {@link keepcalm.mods.blockbreak.PlayerBreakBlockEvent} for blocks broken by players
 * 
 * for example, an RP2 block breaker breaking a block would trigger this event
 * 
 * @author keepcalm
 *
 */
@Cancelable
public class BlockDestroyEvent extends Event {
	public final World world;
	public final int x;
	public final int y;
	public final int z;
	
	public final int blockId;
	public final int blockMeta;
	
	public BlockDestroyEvent(World w, int posX, int posY, int posZ, int blockID, int meta) {
		
		world = w;
		x = posX;
		y = posY;
		z = posZ;
		blockId = blockID;
		blockMeta = meta;
		
	}
	
}
