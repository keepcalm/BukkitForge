package keepcalm.mods.bukkit.events;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class DispenseItemEvent extends Event {

	public final int blockX;
	public final int blockZ;
	public final int blockY;
	
	public final World blockWorld;
	
	/**
	 * The stack that the item will be taken from. After dispensing, the stack will have one
	 * less than the current number of items in it.
	 */
	public final ItemStack stackToDispense;
	
	public DispenseItemEvent(int x, int y, int z, World world, ItemStack stack) {
		blockX = x;
		blockY = y;
		blockZ = z;
		blockWorld = world;
		stackToDispense = stack;
	}
	
	
	
}
