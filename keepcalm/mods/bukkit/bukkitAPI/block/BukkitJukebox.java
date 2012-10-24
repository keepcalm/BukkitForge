package keepcalm.mods.bukkit.bukkitAPI.block;

import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import net.minecraft.src.BlockJukeBox;
import net.minecraft.src.TileEntityRecordPlayer;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
//import org.bukkit.craftbukkit.BukkitWorld;

public class BukkitJukebox extends BukkitBlockState implements Jukebox {
    private final BukkitWorld world;
    private final TileEntityRecordPlayer jukebox;

    public BukkitJukebox(final Block block) {
        super(block);

        world = (BukkitWorld) block.getWorld();
        jukebox = (TileEntityRecordPlayer) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Material getPlaying() {
        return Material.getMaterial(jukebox.record);
    }

    public void setPlaying(Material record) {
        if (record == null) {
            record = Material.AIR;
        }
        jukebox.record = record.getId();
        jukebox.updateEntity();
        if (record == Material.AIR) {
            world.getHandle().setBlockMetadata(getX(), getY(), getZ(), 0);
        } else {
            world.getHandle().setBlockMetadata(getX(), getY(), getZ(), 1);
        }
        world.playEffect(getLocation(), Effect.RECORD_PLAY, record.getId());
    }

    public boolean isPlaying() {
        return getRawData() == 1;
    }

    public boolean eject() {
        boolean result = isPlaying();
        ((BlockJukeBox) net.minecraft.src.Block.jukebox).ejectRecord(world.getHandle(), getX(), getY(), getZ());
        return result;
    }
}
