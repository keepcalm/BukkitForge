package keepcalm.mods.events.forgeex;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

@Cancelable
public class LightningStrikeEvent extends EntityEvent {

	public final int x,y,z;
	public final World world;
	
	public final EntityLightningBolt bolt;
	
	public LightningStrikeEvent(EntityLightningBolt entity, World world, int x, int y, int z) {
		super(entity);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.bolt = entity;
	}
	
}
