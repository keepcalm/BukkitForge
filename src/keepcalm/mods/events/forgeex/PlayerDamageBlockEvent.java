package keepcalm.mods.events.forgeex;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;
/**
 * 
 * Called from ItemInWorldManager.updateBlockRemoving
 * 
 * The cancellation doesn't actually work.
 * @author keepcalm
 *
 */
@Cancelable
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
		return String.format("PlayerDamageBlockEvent{x=%s, y=%s, z=%s, dim=%s, id=%s, damageLeft=%s, damageTaken=%s}", new Object[] {blockX, blockY, blockZ, world.getWorldInfo().getVanillaDimension(), blockID, damageRemaining, damageTaken});
	}

}
