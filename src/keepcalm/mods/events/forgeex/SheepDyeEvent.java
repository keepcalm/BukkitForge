package keepcalm.mods.events.forgeex;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class SheepDyeEvent extends Event {

	public final int newColour;
	public final int oldColour;
	
	public final EntitySheep sheep;
	
	public SheepDyeEvent(EntitySheep sheep, int newColour, int oldColour) {
		this.newColour = newColour;
		this.oldColour = oldColour;
		
		this.sheep = sheep;
	}
	
}
