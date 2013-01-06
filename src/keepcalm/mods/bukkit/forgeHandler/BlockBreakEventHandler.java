package keepcalm.mods.bukkit.forgeHandler;

import keepcalm.mods.blockbreak.PlayerBreakBlockEvent;
import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBlock;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;

import org.bukkit.Bukkit;


/**
 * Needs to be a separate class in case
 * BlockBreakEvent mod isn't installed
 * @author keepcalm
 *
 */
public class BlockBreakEventHandler {
	@ForgeSubscribe
	public void onBlockBreak(PlayerBreakBlockEvent ev) {
		EntityPlayerMP fp;
		if (ev.player instanceof EntityPlayerMP) {
			fp = (EntityPlayerMP) ev.player;
		}
		else {
			fp = BukkitContainer.MOD_PLAYER;
		}
		
		org.bukkit.event.block.BlockBreakEvent bb = new org.bukkit.event.block.BlockBreakEvent(new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.blockX, ev.blockZ)), ev.blockX, ev.blockY, ev.blockZ), new BukkitPlayer(fp));
		Bukkit.getPluginManager().callEvent(bb);
		if (bb.isCancelled()) {
			ev.setCanceled(true);
		}
	}
}
