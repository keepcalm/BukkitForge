package keepcalm.mods.bukkit.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerUseItemEvent extends PlayerEvent {

	public final ItemStack stack;
	public final EntityPlayer player;
	public final World world;
	public final int x;
	public final int y;
	public final int z;
	public final ForgeDirection dir;
	
	public PlayerUseItemEvent(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, ForgeDirection blockFace) {
		super(player);
		this.stack = stack;
		this.player = player;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = blockFace;
		
	}
	
	@Override
	public String toString() {
		return "PlayerUseItemEvent{stack=" + stack.toString() + ",player=" + player.username + ",world=" + world.getWorldInfo().getDimension() + "xCoord=" + x + "yCoord=" + y + "zCoord=" + z + "blockface=" + dir.name();
	}

}
