package keepcalm.mods.events.forgeex;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class LiquidFlowEvent extends Event {

	/**
	 * The (still) version of this liquid
	 */
	public final Block liquid;
	
	public final int flowToX;
	public final int flowToY;
	public final int flowToZ;
	
	public final World world;
	
	public final int flowFromX;
	public final int flowFromY;
	public final int flowFromZ;
	
	public LiquidFlowEvent(Block theLiquid, World w, int newX, int newY, int newZ, int fromX, int fromY, int fromZ) {
		liquid = theLiquid;
		flowToX = newX;
		flowToY = newY;
		flowToZ = newZ;
		
		world = w;
		
		flowFromX = fromX;
		flowFromY = fromY;
		flowFromZ = fromZ;
		
		
	}
	
}
