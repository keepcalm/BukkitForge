package keepcalm.mods.events.forgeex;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class SignChangeEvent extends Event {
	public final int x;
	public final int y;
	public final int z;
	
	public String[] lines;
	
	public final EntityPlayer signChanger;
	
	public SignChangeEvent(int signX, int signY, int signZ, EntityPlayer editor, String[] signText) {
		x = signX;
		y = signY;
		z = signZ;
		lines = signText;
		
		signChanger = editor;
	}
	
	
}
