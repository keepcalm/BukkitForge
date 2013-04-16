package keepcalm.mods.events.forgeex;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
public class PlayerMoveEvent extends PlayerEvent {
	public final int oldX;
	public final int oldY;
	public final int oldZ;
	public final int newX;
	public final int newY;
	public final int newZ;
	
	public final boolean flying;

	public PlayerMoveEvent(EntityPlayer player, int oldX, int oldY, int oldZ, int newX, int newY, int newZ, boolean flying) {
		super(player);
		this.oldX = oldX;
		this.oldY = oldY;
		this.oldZ = oldZ;
		this.newX = newX;
		this.newY = newY;
		this.newZ = newZ;
		
		this.flying = flying;
		
	}

}
