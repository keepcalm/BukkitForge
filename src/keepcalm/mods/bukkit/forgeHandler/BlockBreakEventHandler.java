package keepcalm.mods.bukkit.forgeHandler;

import org.bukkit.Bukkit;

import keepcalm.mods.blockbreak.BlockBreakEvent;
import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBlock;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;


/**
 * Needs to be a separate class in case
 * BlockBreakEvent mod isn't installed
 * @author keepcalm
 *
 */
public class BlockBreakEventHandler {
	@ForgeSubscribe
	public void onBlockBreak(BlockBreakEvent ev) {
		// not cancelable at present
		org.bukkit.event.block.BlockBreakEvent bb = new org.bukkit.event.block.BlockBreakEvent(new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.blockX, ev.blockZ)), ev.blockX, ev.blockY, ev.blockZ), new BukkitPlayer((EntityPlayerMP) ev.player));
		Bukkit.getPluginManager().callEvent(bb);
	}
}
