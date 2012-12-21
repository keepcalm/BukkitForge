package keepcalm.mods.bukkit.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
/**
 * 
 * Easiest way to cancel - just reverse it, it's called AFTER the event does its stuff.
 * 
 * Called from ItemInWorldManager.updateBlockRemoving (after this.durabilityRemainingOnBlock = var5;)
 * 
 * @author keepcalm
 *
 */
public class PlayerDamageBlockEvent extends PlayerEvent {

	public final int blockX;
	public final int blockZ;
	public final int blockY;
	public final int blockID;
	
	/**
	 * Damage taken so far by the block
	 */
	public final int damageTaken;
	/**
	 * Damage until the block breaks
	 */
	public final int damageRemaining;
	
	public final World world;
	
	public PlayerDamageBlockEvent(EntityPlayer player, int x, int y, int z, World playerWorld, int damageDone, int damageLeft) {
		super(player);
		blockX = x;
		blockY = y;
		blockZ = z;
		world = playerWorld;
		blockID = world.getBlockId(x, y, z);
		damageRemaining = damageLeft;
		damageTaken = damageDone;
	}
	
	@Override
	public String toString() {
		return String.format("PlayerDamageBlockEvent{x=%s, y=%s, z=%s, dim=%s, id=%s, damageLeft=%s, damageTaken=%s}", new Object[] {blockX, blockY, blockZ, world.getWorldInfo().getDimension(), blockID, damageRemaining, damageTaken});
	}

}
