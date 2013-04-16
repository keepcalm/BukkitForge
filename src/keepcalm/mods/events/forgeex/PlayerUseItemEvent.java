package keepcalm.mods.events.forgeex;

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
	
	/**
	 * This is called AFTER the item is used, so the easiest way to cancel it is to close the player's window/
	 * break the block/whatever
	 * <p />
	 * The way to use this is to add it to your Forge event handler, same as any other standard forge event.
	 *
	 * @param stack - the ItemStack being used
	 * @param player - the player using the item
	 * @param world - the world the player is in
	 * @param x - the x coordinate of the block being placed ON
	 * @param y - the y coordinate of the block being placed ON
	 * @param z - the z coordinate of the block being placed ON
	 * @param blockFace - the block face being placed on
	 */
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
		return "keepcalm.mods.bukkit.events.PlayerUseItemEvent{stack=" + stack.getItem().getUnlocalizedName() + ", player=" + player.username + ", world=" + world.getWorldInfo().getDimension() + ", xCoord=" + x + ", yCoord=" + y + ", zCoord=" + z + ", blockface=" + dir.name();
	}

}
