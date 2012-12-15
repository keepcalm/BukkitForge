package keepcalm.mods.bukkit.bukkitAPI.block;

import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryBrewer;
import net.minecraft.command.WrongUsageException;
import net.minecraft.tileentity.TileEntityBrewingStand;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
//import org.bukkit.craftbukkit.BukkitWorld;
//import org.bukkit.craftbukkit.inventory.BukkitInventoryBrewer;

public class BukkitBrewingStand extends BukkitBlockState implements BrewingStand {
    private final BukkitWorld world;
    private final TileEntityBrewingStand brewingStand;

    public BukkitBrewingStand(Block block) {
        super(block);

        world = (BukkitWorld) block.getWorld();
        brewingStand = (TileEntityBrewingStand) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public BukkitBrewingStand(int xCoord, int yCoord, int zCoord, net.minecraft.src.World world) {
    	
    	super(new BukkitBlock(new BukkitChunk(world.getChunkFromBlockCoords(xCoord, zCoord)), xCoord, yCoord, zCoord));
    	
        if (world.getBlockId(xCoord, yCoord, zCoord) != net.minecraft.src.Block.brewingStand.blockID) {
        	throw new WrongUsageException(String.format("No brewing stand at %s,%s,%s!", new Object[] {xCoord, yCoord, zCoord})) ;
        }
        
        this.brewingStand = (TileEntityBrewingStand) world.getBlockTileEntity(xCoord, yCoord, zCoord);
        this.world = (BukkitWorld) Bukkit.getServer().getWorld(world.getWorldInfo().getWorldName());
	}

	public BrewerInventory getInventory() {
        return new BukkitInventoryBrewer(brewingStand);
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        if (result) {
            brewingStand.updateEntity();
        }

        return result;
    }

    public int getBrewingTime() {
        return brewingStand.getBrewTime();
    }

    public void setBrewingTime(int brewTime) {
        brewingStand.setBrewTime(brewTime);
    }
}
