package keepcalm.mods.events.forgeex;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;


@Cancelable
public class PressurePlateInteractEvent extends EntityEvent {

	public final BlockPressurePlate plate;
	public final World world;
	
	public final int x;
	public final int y;
	public final int z;
	
	public PressurePlateInteractEvent(Entity entity, BlockPressurePlate plate, World world,
			int x, int y, int z) {
		super(entity);
		
		this.plate = plate;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		
		
		
	}

}
